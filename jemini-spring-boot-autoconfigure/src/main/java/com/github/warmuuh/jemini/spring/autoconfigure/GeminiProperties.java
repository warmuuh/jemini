package com.github.warmuuh.jemini.spring.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gemini.server")
public class GeminiProperties {
  int port = 1965;
  Resource keystore;
  String keystorePassword = "";
  String keyPassword = "";
  String keyAlias = null;
  boolean dualHttp = false;
}
