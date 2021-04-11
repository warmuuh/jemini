package com.github.warmuuh.jemini.example;

import com.github.warmuuh.jemini.spring.GeminiInput;
import javax.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {

  @GetMapping("/index")
  public String index() {
    return "index";
  }

  @GetMapping("/responseBody")
  @ResponseBody
  public String responseBody() {
    return "Hello World. this is a dynamic page: " + Math.random();
  }

  @GetMapping("/test2")
  public String test2() {
    return "page";
  }


  @GetMapping("/status")
  public ResponseEntity<String> withCustomStatus(){
   return ResponseEntity.status(10).body("input");
  }

  @GetMapping("/input")
  @ResponseBody
  public String withInput(@GeminiInput(value = "testValue", sensitive = true) String userInput){
    return "You Wrote: " + userInput;
  }

  @GetMapping("/cert")
  @ResponseBody
  public String withClientCertRequest(HttpSession session) {
    Integer attr = (Integer)session.getAttribute("sessionAttr");
    if (attr == null){
      attr = 0;
    }
    session.setAttribute("sessionAttr", attr + 1);
    return "Client certified. \nrequests made: "+ attr+"\nhash (sessionId): " + session.getId();
  }



}
