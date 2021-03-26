package com.github.warmuuh.jemini;

import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConfiguration.Customizer;
import org.eclipse.jetty.server.Request;

public class SessionIdByClientCertCustomizer implements Customizer {

  @Override
  public void customize(Connector connector, HttpConfiguration channelConfig, Request request) {
    var certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
    if (certs != null && certs.length > 0) {
      try {
        var thumbPrint = Hex.encodeHex(MessageDigest.getInstance("SHA-1").digest(certs[0].getEncoded()));
        request.setRequestedSessionId(new String(thumbPrint));
      } catch (Exception e){
        e.printStackTrace();
      }
    }
  }
}
