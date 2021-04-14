package com.github.warmuuh.geminiclient;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
public abstract class GeminiResponse {
	private final GeminiStatus status;
	private final String meta;
	protected final Flux<byte[]> body;


	public abstract Mono<byte[]> contentAsBytes();
	public abstract Mono<String> contentAsString();
}
