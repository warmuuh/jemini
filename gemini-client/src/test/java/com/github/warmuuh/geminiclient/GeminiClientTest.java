package com.github.warmuuh.geminiclient;


class GeminiClientTest {


	public static void main(String[] args) throws InterruptedException {
		var response = GeminiClient.of("gemini://localhost").get("/").block();
		System.out.println("#### RECEIVED: " + response.getStatus() + " " + response.getMeta());
		System.out.println("#### RECEIVED BODY: " + response.contentAsString().block());
	}

}