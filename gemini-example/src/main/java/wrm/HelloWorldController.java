package wrm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

}
