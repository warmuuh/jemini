package com.github.warmuuh.jemini.spring.autoconfigure;

import com.github.warmuuh.jemini.spring.GeminiInputHandlerMethodArgumentResolver;

import java.io.IOException;
import java.security.KeyStore;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.warmuuh.jemini.spring.InputFormProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

@Configuration
@AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration")
public class GeminiAutoConfiguration implements WebMvcConfigurer {

  @Autowired
  GeminiProperties properties;

  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    resolvers.add(new ResponseStatusExceptionResolver(){
      @Override
      protected ModelAndView applyStatusAndReason(int statusCode, String reason, HttpServletResponse response)
          throws IOException {
        response.sendError(statusCode, reason);
        return new ModelAndView();
      }
    });
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    var inputFormProvider = new InputFormProvider();
    inputFormProvider.setCssFile(properties.cssForHttp);
    resolvers.add(new GeminiInputHandlerMethodArgumentResolver(inputFormProvider));
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    if(properties.isDualHttp()){
      //gemini:// requests will set ACCEPT-HEADER automatically
      configurer.defaultContentType(MediaType.parseMediaType("text/html; charset=utf-8"));
    } else {
      configurer.defaultContentType(MediaType.parseMediaType("text/gemini; charset=utf-8"));
    }
  }

  @Bean @Primary
  public ConfigurableServletWebServerFactory webServerFactory() throws Exception {
    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    factory.setPort(properties.getPort());

    var jks = KeyStore.getInstance("JKS");
    jks.load(properties.getKeystore().getInputStream(), properties.getKeystorePassword().toCharArray());

    factory.addServerCustomizers(new GeminiJettyServerCustomizer(jks, properties));
    return factory;
  }

}
