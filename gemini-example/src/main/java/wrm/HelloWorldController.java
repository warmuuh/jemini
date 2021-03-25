package wrm;

import com.github.warmuuh.GeminiInput;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {

  @GetMapping("/test")
  public String test() {
    return "index";
  }

  @GetMapping("/test1")
  @ResponseBody
  public String test1() {
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

}
