package com.github.warmuuh.geminiclient.netty;

import com.github.warmuuh.geminiclient.GeminiRequest;
import com.github.warmuuh.geminiclient.GeminiResponse;
import com.github.warmuuh.geminiclient.GeminiStatus;
import com.github.warmuuh.geminiclient.RequestExecutor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.SslProvider;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class NettyRequestExecutor implements RequestExecutor {


	public static final byte[] CRLF = "\r\n".getBytes();

	@Override
	public Mono<GeminiResponse> execute(URI baseUri, GeminiRequest request) {

		var requestedUri = baseUri.resolve(request.getPath());

		return Mono.from(sink -> {
			TcpClient client = null
					;
			try {
				client = TcpClient.create()
						.host(baseUri.getHost())
						.port(baseUri.getPort() == -1 ? 1965 : baseUri.getPort())
						.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
						.secure(SslProvider.builder().sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()).build())
						.wiretap("reactor.netty.tcp.TcpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8)
						.handle((inbound, outbound) -> sendRequestAndWaitForResponse(requestedUri, inbound, outbound, sink));
			} catch (SSLException e) {
				throw new RuntimeException(e);
			}

			client.connect()
					.subscribe();
		});
	}

	private Mono<Void> sendRequestAndWaitForResponse(URI requestedUri, NettyInbound inbound, NettyOutbound outbound, Subscriber<? super GeminiResponse> sink) {
		return outbound.sendString(Mono.just(requestedUri.toString()))
				.sendByteArray(Mono.just(CRLF))
				.then(inbound.receive().then())
				.then();
	}

	private Mono<Void> receive(NettyInbound inbound, Subscriber<? super GeminiResponse> sink) {
		return Flux.defer(() -> {
			var inboundData = inbound.receive();
			sink.onNext(new NettyGeminiResponse(GeminiStatus.SUCCESS, "", inboundData));
			return inboundData;
		}).then(Mono.defer(() -> {
			sink.onComplete();
			return null;
		}));
	}
}
