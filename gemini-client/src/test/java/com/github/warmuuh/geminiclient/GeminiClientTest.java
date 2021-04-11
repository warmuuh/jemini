package com.github.warmuuh.geminiclient;


class GeminiClientTest {


	public static void main(String[] args) {
		var response = GeminiClient.of("gemini://wrm.hopto.org").get("/").block();
		System.out.println("#### RECEIVED: ");
		System.out.println("#### RECEIVED BODY: " + response.contentAsString().block());
	}

}