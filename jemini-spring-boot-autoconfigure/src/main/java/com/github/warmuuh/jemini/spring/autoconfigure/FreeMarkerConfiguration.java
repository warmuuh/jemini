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
@ConditionalOnProperty(
    name = {"gemini.server.dualHttp"},
    matchIfMissing = false
)
public class FreeMarkerConfiguration {

  @Autowired(required = false)
  private FreeMarkerViewResolver freeMarkerViewResolver;

  @Bean
  @ConditionalOnProperty(
      name = {"spring.freemarker.enabled"},
      matchIfMissing = true
  )
  GmiToHtmlViewResolver freeMarkerHtmlViewResolver(GeminiProperties properties, FreeMarkerViewResolver freemarker) {
    return new GmiToHtmlViewResolver(properties.getCssForHttp(), freemarker);
  }


  @PostConstruct
  public void setup(){
    freeMarkerViewResolver.setContentType("text/gemini; charset=UTF-8");
  }

}
