package com.github.warmuuh.jemini.spring.autoconfigure;

import com.github.warmuuh.jemini.GeminiServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;

import javax.net.ssl.TrustManager;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.util.Collection;
import java.util.LinkedList;

class GeminiJettyServerCustomizer implements JettyServerCustomizer {
	private final KeyStore jks;
	private GeminiProperties properties;

	public GeminiJettyServerCustomizer(KeyStore jks, GeminiProperties properties) {
		this.jks = jks;
		this.properties = properties;
	}

	@Override
	public void customize(Server server) {
		SslContextFactory.Server sslContextFactory = createSslContext();

		var connectors = new LinkedList<>();
		connectors.add(createGeminiConnector(server, sslContextFactory));

		if (properties.isDualHttp()) {
			connectors.add(createHttpsConnector(server, sslContextFactory));

			if (properties.isRedirectToHttps()) {
				connectors.add(createHttpConnector(server));

				HandlerList handlerList = new HandlerList();
				ResourceHandler resourceHandler = createHttpResourceHandler();
				handlerList.addHandler(resourceHandler);
				handlerList.addHandler(new SecuredRedirectHandler());
				handlerList.addHandler(server.getHandler());
				server.setHandler(handlerList);
			}
		}

		server.setConnectors(connectors.toArray(new Connector[0]));
		server.setSessionIdManager(new GeminiSessionIdManager(server));
	}

	/**
	 * used to serve some resources directly over http, such as a secret for lets encrypt
	 *
	 */
	private ResourceHandler createHttpResourceHandler() {
		var resourceHandler = new ResourceHandler() {
			@Override
			public Resource getResource(String path) {
				var resource = super.getResource(path);
				var isDirectory = resource != null && resource.isDirectory();
				return isDirectory ? null : resource;
			}
		};
		resourceHandler.setBaseResource(Resource.newClassPathResource(properties.getStaticHttpDirectory()));
		resourceHandler.setDirAllowed(false);
		resourceHandler.setWelcomeFiles(null);
		return resourceHandler;
	}

	private ServerConnector createHttpConnector(Server server) {
		HttpConfiguration config = new HttpConfiguration();
		config.setSecurePort(properties.getHttpsPort());
		config.setSecureScheme("https");
		ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(config));
		httpConnector.setPort(properties.getHttpPort());
		return httpConnector;
	}

	private ServerConnector createHttpsConnector(Server server, SslContextFactory.Server sslContextFactory) {
		HttpConfiguration httpsConf = new HttpConfiguration();
		httpsConf.addCustomizer(new SecureRequestCustomizer());

		var sslHttpConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(httpsConf));
		sslHttpConnector.setPort(properties.getHttpsPort());
		return sslHttpConnector;
	}

	private ServerConnector createGeminiConnector(Server server, SslContextFactory.Server sslContextFactory) {
		var sslGeminiConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "gemini"), new GeminiServerConnectionFactory());
		sslGeminiConnector.setPort(properties.getPort());
		return sslGeminiConnector;
	}

	private SslContextFactory.Server createSslContext() {
		SslContextFactory.Server sslContextFactory = new SslContextFactory.Server() {
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

		if (properties.getKeyAlias() != null) {
			sslContextFactory.setCertAlias(properties.getKeyAlias());
		}
		sslContextFactory.setKeyStore(jks);
		return sslContextFactory;
	}

}
