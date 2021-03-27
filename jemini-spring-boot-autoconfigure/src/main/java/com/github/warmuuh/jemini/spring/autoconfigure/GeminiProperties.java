package com.github.warmuuh.jemini.spring.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gemini.server")
public class GeminiProperties {
  int port = 1965;
  String keystore = "keystore.jks";
  String keystorePassword = "";
  String keyPassword = "";
  String keyAlias = null;
  boolean dualHttp = false;
}
