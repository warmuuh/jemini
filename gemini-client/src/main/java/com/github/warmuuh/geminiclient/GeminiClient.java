package com.github.warmuuh.geminiclient;

import com.github.warmuuh.geminiclient.netty.NettyRequestExecutor;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor
public class GeminiClient {
	private final URI baseUri;
	private final RequestExecutor executor = new NettyRequestExecutor();
	private final KeyManagerFactory keyManagerFactory;

	public static GeminiClient of(String baseUri){
		return of(baseUri, null, null);
	}

	public static GeminiClient of(String baseUri, KeyStore keyStore, String keystorePassword){
		return new GeminiClient(URI.create(baseUri), loadKeyManager(keyStore, keystorePassword));
	}

	@SneakyThrows
	private static KeyManagerFactory loadKeyManager(KeyStore keyStore, String keystorePassword) {
		if (keyStore == null){
			return null;
		}
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, keystorePassword.toCharArray());
		return keyManagerFactory;
	}


	public Mono<GeminiResponse> get(String path) {
		return execute(new GeminiRequest(path));
	}

	public Mono<GeminiResponse> execute(GeminiRequest request){
		return executor.execute(baseUri, request, keyManagerFactory);
	}
}
