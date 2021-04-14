# A Client for Gemini Protocol

this is a client-library for the gemini protocol, based on netty and reactor.

supported features:
* reactive, supports chunk-based responses (think [SSE](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events))
* customizable technical client implementation
* client-certificate support

# how to use

```xml
<!-- setup github repository as maven repo. see https://docs.github.com/en/packages/guides/configuring-apache-maven-for-use-with-github-packages -->
<dependencies>
  <dependency>
   <groupId>com.github.warmuuh.jemini</groupId>
   <artifactId>gemini-client</artifactId>
   <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

```java
    GeminiResponse response = GeminiClient.of("gemini://localhost")
        .get("/index").block();

    System.out.println(response.getStatus());
    System.out.println(response.contentAsString().block());
```

or with usage of client certificate

```java
    KeyStore jks = KeyStore.getInstance("JKS");
    jks.load(new FileInputStream("keystore.jks"), "storepassword".toCharArray());

    var clientWithCert = GeminiClient.of("gemini://localhost", jks, "storepassword");
    var response = clientWithCert.get("/cert").block();
    assertThat(response.getStatus()).isEqualTo(GeminiStatus.SUCCESS);
    assertThat(response.getMeta()).isEqualTo("text/gemini;charset=UTF-8");
    assertThat(response.contentAsString().block()).contains("Client certified.");
```
