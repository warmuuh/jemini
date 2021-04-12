package com.github.warmuuh.geminiclient.netty;

import com.github.warmuuh.geminiclient.GeminiResponse;
import com.github.warmuuh.geminiclient.GeminiStatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
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
	public Mono<String> contentAsString() {
		return body
				.map(buf -> new String(buf, StandardCharsets.UTF_8))
				.collect(Collectors.joining());
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
