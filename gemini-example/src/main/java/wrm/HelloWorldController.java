package wrm;

import com.github.warmuuh.GeminiInput;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

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

  @GetMapping("/cert2")
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
