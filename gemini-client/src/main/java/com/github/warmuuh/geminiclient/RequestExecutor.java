package com.github.warmuuh.geminiclient;

import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import reactor.core.publisher.Mono;

import java.net.URI;

public interface RequestExecutor {

	Mono<GeminiResponse> execute(URI baseUri, GeminiRequest request, KeyManagerFactory keyManagerFactory);
}
