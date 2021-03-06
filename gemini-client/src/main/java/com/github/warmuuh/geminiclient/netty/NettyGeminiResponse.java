package com.github.warmuuh.geminiclient.netty;

import com.github.warmuuh.geminiclient.GeminiResponse;
import com.github.warmuuh.geminiclient.GeminiStatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.util.stream.Collectors;

public class NettyGeminiResponse extends GeminiResponse {

	public NettyGeminiResponse(GeminiStatus status, String meta, Flux<byte[]> body) {
		super(status, meta, body);
	}

	@Override
	public Mono<byte[]> contentAsBytes() {
		return body.collectList().map(list -> list.stream()
					.collect(
							() -> new ByteArrayOutputStream(),
							(b, e) -> {
								try {
									b.write(e);
								} catch (IOException e1) {
									throw new RuntimeException(e1);
								}
							},
							(a, b) -> {}).toByteArray());
	}

	@Override
	public Mono<String> contentAsString() {
		return contentAsBytes().map(buf -> new String(buf, StandardCharsets.UTF_8));
//				.map(buf -> new String(buf, StandardCharsets.UTF_8))
//				.collect(Collectors.joining());
//				.collectList()
//				.map((List<ByteBuf> bufs) -> convertToString(bufs));
//		return nettyBody.asString().collect(Collectors.joining());
	}


//
//	public String convertToString(List<ByteBuf> bufs){
//		bufs.stream().map(buf -> buf.)
//		CompositeByteBuf compBuf = ByteBufAllocator.DEFAULT.compositeBuffer().addComponents(true, bufs.toArray(new ByteBuf[0]));
//		String content = compBuf.readCharSequence(compBuf.readableBytes(), StandardCharsets.UTF_8).toString();
//		return content;
//	}

}
