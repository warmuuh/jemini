package com.github.warmuuh.jemini.spring.autoconfigure;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class FreeMarkerConfiguration {

  @Bean
  @ConditionalOnProperty(
          name = {"spring.freemarker.enabled"},
          matchIfMissing = true
  )
  FreeMarkerViewResolver freeMarkerHtmlViewResolver(FreeMarkerProperties properties) {
    FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
    properties.applyToMvcViewResolver(resolver);
    return resolver;
  }

  @Bean
  @ConditionalOnProperty(
          name = {"spring.freemarker.enabled"},
          matchIfMissing = true
  )
  FreeMarkerViewResolver freeMarkerGmiViewResolver(FreeMarkerProperties properties) {
    FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
    properties.applyToMvcViewResolver(resolver);
    resolver.setContentType("text/gemini; charset=utf-8");
    return resolver;
  }

}
