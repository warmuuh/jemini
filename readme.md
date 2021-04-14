# jemini
gemini protocol server on top of jetty.

# Features

* integration with jetty & spring boot
* supports all the goodies of normal spring boot webapp
* implicit input handling with `@GmiInput` parameter annotation
* dual-protocol: can serve both `gemini://` and `https://` at the same time, translating gemtext to html
* auto-redirect http to https
* input handling in both `gemini://` and `https://`, transparent to application
* client certificates for both  `gemini://` and `https://`
* session-logic based on client-certificates, both for  `gemini://` and `https://`
* css-support for html-rendered gemtext


# Getting Started

## generate certificate
```
keytool -genkey -alias <alias> -keyalg RSA -keypass <key passphrase> -storepass <store passphrase> -keystore keystore.jks
```

```yaml
# application.yaml
gemini:
  server:
    key-password: storepassword
    keystore-password: storepassword
    keystore: file:keystore.jks
    dualHttp: true
    css-for-http: /classless-tiny.css
```


```xml
<!-- setup github repository as maven repo. see https://docs.github.com/en/packages/guides/configuring-apache-maven-for-use-with-github-packages -->
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

