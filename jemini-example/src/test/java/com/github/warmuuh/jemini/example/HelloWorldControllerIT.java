package com.github.warmuuh.jemini.example;

import com.github.warmuuh.geminiclient.GeminiClient;
import com.github.warmuuh.geminiclient.GeminiStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
class HelloWorldControllerIT {


  @Test
  void shouldServeIndex(){
    var response = GeminiClient.of("gemini://localhost").get("/index").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SUCCESS);
    assertThat(response.getMeta()).isEqualTo("text/gemini; charset=UTF-8");

  }



}