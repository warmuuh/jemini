package com.github.warmuuh.geminiclient;

import reactor.core.publisher.Mono;

import java.net.URI;

public interface RequestExecutor {

	Mono<GeminiResponse> execute(URI baseUri, GeminiRequest request);
}
