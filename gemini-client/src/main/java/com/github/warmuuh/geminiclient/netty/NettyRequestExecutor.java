package com.github.warmuuh.geminiclient.netty;

import com.github.warmuuh.geminiclient.GeminiRequest;
import com.github.warmuuh.geminiclient.GeminiResponse;
import com.github.warmuuh.geminiclient.GeminiStatus;
import com.github.warmuuh.geminiclient.RequestExecutor;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;
import reactor.core.publisher.Sinks.Many;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.SslProvider;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

public class NettyRequestExecutor implements RequestExecutor {


  public static final byte[] CRLF = "\r\n".getBytes();

  @Override
  public Mono<GeminiResponse> execute(URI baseUri, GeminiRequest request) {

    var requestedUri = baseUri.resolve(request.getPath());

    return Mono.create(sink -> {
      TcpClient client = null;
      try {
        client = TcpClient.create()
            .host(baseUri.getHost())
            .port(baseUri.getPort() == -1 ? 1965 : baseUri.getPort())
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .secure(SslProvider.builder()
                .sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                .build())
//            .wiretap("reactor.netty.tcp.TcpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL,
//                StandardCharsets.UTF_8)
            .handle((inbound, outbound) -> sendRequestAndWaitForResponse(requestedUri, inbound, outbound, sink));
      } catch (SSLException e) {
        throw new RuntimeException(e);
      }

      client.connect()
          .subscribe();
    });
  }

  private Mono<Void> sendRequestAndWaitForResponse(URI requestedUri, NettyInbound inbound, NettyOutbound outbound,
      MonoSink<? super GeminiResponse> sink) {

    var spec = Sinks.many().replay().<byte[]>all();
    Many<byte[]> responses = spec;

    var cachedResponse = Flux.from(inbound.receive()).cache();

    //TODO really always first chunk?!
    cachedResponse.take(1).subscribe(b -> {
          String header = b.readCharSequence(b.readableBytes(), StandardCharsets.UTF_8).toString();
          int splitIdx = header.indexOf(' ');
          String status = header.substring(0, splitIdx);
          String meta = header.substring(splitIdx + 1);
          sink.success(
              new NettyGeminiResponse(GeminiStatus.tryFromStatus(Integer.parseInt(status)), meta, responses.asFlux()));
        },
        err -> sink.error(err),
        () -> sink.success());

    //rest goes into body
    cachedResponse.skip(1)
        .subscribe(
            n -> {
              byte[] bytes = new byte[n.readableBytes()];
              n.readBytes(bytes);
              responses.emitNext(bytes, EmitFailureHandler.FAIL_FAST);
            },
            err -> responses.emitError(err, EmitFailureHandler.FAIL_FAST),
            () -> responses.emitComplete(EmitFailureHandler.FAIL_FAST)
        );

//		sink.success(new NettyGeminiResponse(GeminiStatus.SUCCESS, "", responses.asFlux()));

    NettyOutbound then = outbound.sendString(Mono.just(requestedUri + "\r\n")).then(cachedResponse.then());
    return then.then();
  }

//	private Mono<Void> receive(NettyInbound inbound, Subscriber<? super GeminiResponse> sink) {
//		return Flux.defer(() -> {
//			var inboundData = inbound.receive();
//			sink.onNext(new NettyGeminiResponse(GeminiStatus.SUCCESS, "", inboundData));
//			return inboundData;
//		}).then(Mono.defer(() -> {
//			sink.onComplete();
//			return null;
//		}));
//	}
}
