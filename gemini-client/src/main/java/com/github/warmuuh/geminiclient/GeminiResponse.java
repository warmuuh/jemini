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
	private final Flux<ByteBuf> body;


	public abstract Mono<String> contentAsString();
}
