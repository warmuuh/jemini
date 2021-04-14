package com.github.warmuuh.jemini.example;

import com.github.warmuuh.geminiclient.GeminiClient;
import com.github.warmuuh.geminiclient.GeminiStatus;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class,
    webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class GeminiProtocolIT {

  private final GeminiClient client = GeminiClient.of("gemini://localhost");

  @Test
  void shouldServeIndex(){
    var response = client.get("/index").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SUCCESS);
    assertThat(response.getMeta()).isEqualTo("text/gemini; charset=UTF-8");
    assertThat(response.contentAsString().block()).contains("# Hello World");
  }

  @Test
  void shouldServeResponseBody(){
    var response = client.get("/responseBody").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SUCCESS);
    assertThat(response.getMeta()).isEqualTo("text/gemini;charset=UTF-8");
    assertThat(response.contentAsString().block()).contains("Hello World.");
  }

  @Test
  void shouldServeCustomStatus(){
    var response = client.get("/status").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.INPUT);
    assertThat(response.getMeta()).isEqualTo("");
  }

  @Test
  void shouldQueryForInputIfNotGive(){
    var response = client.get("/input").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SENSITIVE_INPUT);
    assertThat(response.getMeta()).isEqualTo("testValue");
  }

  @Test
  void shouldAcceptInputIfGiven(){
    var response = client.get("/input?blub").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SUCCESS);
    assertThat(response.getMeta()).isEqualTo("text/gemini;charset=UTF-8");
    assertThat(response.contentAsString().block()).isEqualTo("You Wrote: blub");
  }

  @Test
  void shouldServeStatic(){
    var response = client.get("/sheep.jpg").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SUCCESS);
    assertThat(response.getMeta()).isEqualTo("image/jpeg");
    assertThat(response.contentAsBytes().block()).hasSizeGreaterThan(100);
  }

  @Test
  void shouldSupportRedirect(){
    var response = client.get("/redirect").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.REDIRECT_TEMPORARY);
    assertThat(response.getMeta()).isEqualTo("/index");
  }

  @Test
  void shouldQueryForCert(){
    var response = client.get("/cert").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.CLIENT_CERT_REQUIRED);
    assertThat(response.getMeta()).isEqualTo("Client Certificate Needed");
  }

  @Test
  void shouldAcceptClientCert() throws Exception {
    KeyStore jks = KeyStore.getInstance("JKS");
    jks.load(new FileInputStream("keystore.jks"), "storepassword".toCharArray());

    var clientWithCert = GeminiClient.of("gemini://localhost", jks, "storepassword");
    var response = clientWithCert.get("/cert").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SUCCESS);
    assertThat(response.getMeta()).isEqualTo("text/gemini;charset=UTF-8");
    assertThat(response.contentAsString().block()).contains("Client certified.");
  }

}