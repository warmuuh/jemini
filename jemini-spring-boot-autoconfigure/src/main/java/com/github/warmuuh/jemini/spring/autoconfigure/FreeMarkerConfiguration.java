package com.github.warmuuh.jemini.spring.autoconfigure;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
//@ConditionalOnBean(FreeMarkerViewResolver.class)
public class FreeMarkerConfiguration {

  @Autowired(required = false)
  FreeMarkerViewResolver resolver;

  @PostConstruct
  void setup() {
    if (resolver != null){
      resolver.setContentType("text/gemini; charset=utf-8");
    }
  }
}
