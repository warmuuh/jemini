package com.github.warmuuh.jemini.spring.autoconfigure;

import javax.annotation.PostConstruct;

import com.github.warmuuh.jemini.spring.GmiToHtmlViewResolver;
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

//
//  @Autowired
//  private FreeMarkerProperties properties;

  @Autowired(required = false)
  private FreeMarkerViewResolver freeMarkerViewResolver;

  @Bean
  @ConditionalOnProperty(
          name = {"spring.freemarker.enabled"},
          matchIfMissing = true
  )
  GmiToHtmlViewResolver freeMarkerHtmlViewResolver(FreeMarkerViewResolver freemarker) {
    return new GmiToHtmlViewResolver(freemarker);
  }


  @PostConstruct
  public void setup(){
    freeMarkerViewResolver.setContentType("text/gemini; charset=UTF-8");
  }

//  @Bean
//  @ConditionalOnProperty(
//          name = {"spring.freemarker.enabled"},
//          matchIfMissing = true
//  )
//  FreeMarkerViewResolver freeMarkerGmiViewResolver() {
//    FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
//    properties.applyToMvcViewResolver(resolver);
//    resolver.setContentType("text/gemini; charset=utf-8");
//    return resolver;
//  }

}
