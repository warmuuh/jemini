package com.github.warmuuh.jemini;

import java.util.List;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.AbstractConnectionFactory;
import org.eclipse.jetty.server.Connector;

public class GeminiServerConnectionFactory extends AbstractConnectionFactory {

  public GeminiServerConnectionFactory() {
    super("gemini");
  }

  @Override
  public String getProtocol() {
    return "gemini";
  }

  @Override
  public List<String> getProtocols() {
    return List.of(getProtocol());
  }

  @Override
  public Connection newConnection(Connector connector, EndPoint endPoint) {
    return new GeminiConnection(connector.getByteBufferPool(), getInputBufferSize(), connector, endPoint, connector.getExecutor());
  }


}
