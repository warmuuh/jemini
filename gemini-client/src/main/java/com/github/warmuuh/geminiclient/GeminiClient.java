package com.github.warmuuh.geminiclient;

import com.github.warmuuh.geminiclient.netty.NettyRequestExecutor;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor
public class GeminiClient {
	private final URI baseUri;
	private final RequestExecutor executor = new NettyRequestExecutor();

	public static GeminiClient of(String baseUri){
		return new GeminiClient(URI.create(baseUri));
	}


	public Mono<GeminiResponse> get(String path) {
		return execute(new GeminiRequest(path));
	}

	public Mono<GeminiResponse> execute(GeminiRequest request){
		return executor.execute(baseUri, request);
	}
}
