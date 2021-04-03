package com.github.warmuuh.jemini.spring.autoconfigure;

import com.github.warmuuh.jemini.spring.GeminiInputHandlerMethodArgumentResolver;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import com.github.warmuuh.jemini.GeminiServerConnectionFactory;

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
    resolvers.add(new GeminiInputHandlerMethodArgumentResolver());
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
        var connectors = new LinkedList<>();
        connectors.add(sslGeminiConnector);

        if (properties.isDualHttp()) {
          HttpConfiguration httpsConf = new HttpConfiguration();
          httpsConf.addCustomizer(new SecureRequestCustomizer());

          var sslHttpConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(httpsConf));
          sslHttpConnector.setPort(properties.getHttpsPort());
          connectors.add(sslHttpConnector);

          if (properties.isRedirectToHttps()){
            HttpConfiguration config = new HttpConfiguration();
            config.setSecurePort(properties.getHttpsPort());
            config.setSecureScheme("https");
            ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(config));
            httpConnector.setPort(properties.getHttpPort());
            connectors.add(httpConnector);

            HandlerList handlerList = new HandlerList();
            var resourceHandler = new ResourceHandler();
            resourceHandler.setBaseResource(Resource.newClassPathResource("/static-http"));
            handlerList.addHandler(resourceHandler);
            handlerList.addHandler(new SecuredRedirectHandler());
            handlerList.addHandler(server.getHandler());
            server.setHandler(handlerList);
          }
        }

        server.setConnectors(connectors.toArray(new Connector[0]));
        server.setSessionIdManager(new DefaultSessionIdManager(server){
          @Override
          public String newSessionId(HttpServletRequest request, long created) {
            if (request.getRequestedSessionId() == null){
              throw new ResponseStatusException(60, "Client Certificate Needed", null);
            }
            return request.getRequestedSessionId();
          }
        });

      }
    });
    return factory;
  }
}
