package com.github.warmuuh;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
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
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import wrm.GeminiServerConnectionFactory;

@Configuration
@AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration")
public class GeminiAutoConfiguration implements WebMvcConfigurer {


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
    resolvers.add(new GeminiInputHandlerMethodArgumentResolver());
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.parseMediaType("text/gemini; charset=utf-8"));
  }

  @Bean @Primary
  public ConfigurableServletWebServerFactory webServerFactory(GeminiProperties properties) throws Exception {
    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    factory.setPort(properties.getPort());

    var jks = KeyStore.getInstance("JKS");
    jks.load(new FileInputStream(properties.getKeystore()), properties.getKeystorePassword().toCharArray());

//    factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"));
    factory.addServerCustomizers(new JettyServerCustomizer() {
      @Override
      public void customize(Server server) {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server(){
          @Override
          protected TrustManager[] getTrustManagers(KeyStore trustStore, Collection<? extends CRL> crls)
              throws Exception {
            return TRUST_ALL_CERTS;
          }
        };
        sslContextFactory.setEndpointIdentificationAlgorithm(null);
        sslContextFactory.setKeyStorePassword(properties.getKeystorePassword());
        sslContextFactory.setKeyManagerPassword(properties.getKeystorePassword());
        sslContextFactory.setWantClientAuth(true);

        if (properties.getKeyAlias() != null){
          sslContextFactory.setCertAlias(properties.getKeyAlias());
        }
        sslContextFactory.setKeyStore(jks);

        var sslGeminiConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "gemini"), new GeminiServerConnectionFactory());
        sslGeminiConnector.setPort(properties.getPort());
        server.setConnectors(new Connector[]{ sslGeminiConnector });
      }
    });
    return factory;
  }
}
