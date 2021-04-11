package com.github.warmuuh.geminiclient.netty;

import com.github.warmuuh.geminiclient.GeminiResponse;
import com.github.warmuuh.geminiclient.GeminiStatus;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.util.stream.Collectors;

public class NettyGeminiResponse extends GeminiResponse {
	private ByteBufFlux nettyBody;

	public NettyGeminiResponse(GeminiStatus status, String meta, ByteBufFlux body) {
		super(status, meta, body);
		this.nettyBody = body;
	}

	@Override
	public Mono<String> contentAsString() {
		return nettyBody.asString().collect(Collectors.joining());
	}
}
