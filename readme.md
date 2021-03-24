# gemini spring boot starter
spring boot starter for gemini protocol

# Getting Started
```xml
<dependencies>
  <dependency>
   <groupId>com.github.warmuuh</groupId>
   <artifactId>gemini-spring-boot-starter</artifactId>
   <version>1.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
  </dependency>
</dependencies>
```

then you can use spring mvc as usual
```java
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
}
```

## generate certificate
```
keytool -genkey -alias <alias> -keyalg RSA -keypass <key passphrase> -storepass <store passphrase> -keystore keystore.jks
```