package com.github.warmuuh.jemini;

import com.github.warmuuh.jemini.GeminiProtocolParser.GeminiParserListener;
import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.http.MetaData.Response;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpTransport;
import org.eclipse.jetty.util.Callback;

public class GeminiHttpChannelAdapter extends HttpChannel implements GeminiParserListener {

//  private String query;

  public GeminiHttpChannelAdapter(Connector connector,
      HttpConfiguration configuration, EndPoint endPoint,
      HttpTransport transport) {
    super(connector, configuration, endPoint, transport);
  }

  @Override
  public boolean sendResponse(Response info, ByteBuffer content, boolean complete, Callback callback) {
    var reason = (String) getRequest().getAttribute("javax.servlet.error.message");
    if (reason != null){
      info = new MetaData.Response(getRequest().getHttpVersion(), getResponse().getStatus(), reason, getResponse().getHttpFields(), getResponse().getLongContentLength());
//      info.setTrailerSupplier(getRequest().getTrailers());
    }

    return super.sendResponse(info, content, complete, callback);
  }

  // // pass query as body, so that @RequestBody can work
//  @Override
//  protected HttpInput newHttpInput(HttpChannelState state) {
//    ByteArrayInputStream reader = query != null ? new ByteArrayInputStream(query.getBytes()) : new ByteArrayInputStream(new byte[]{});
//    return new HttpInput(state) {
//
//      @Override
//      public int read() throws IOException {
//        return reader.read();
//      }
//
//      @Override
//      public int readLine(byte[] b, int off, int len) throws IOException {
//        return reader.read(b, off, len);
//      }
//
//      @Override
//      public boolean markSupported() {
//        return reader.markSupported();
//      }
//
//      @Override
//      public synchronized void mark(int readlimit) {
//        reader.mark(readlimit);
//      }
//
//      @Override
//      public synchronized void reset() throws IOException {
//        reader.reset();
//      }
//    };
//  }

  @Override
  public void startRequest(String schema, String host, String path, String query) {
    getRequest().setSecure(true);
    getRequest().setScheme(schema);
    getRequest().setServletPath(path);
    getRequest().setQueryString(query);
//    this.query = query;

    String uri = path.isEmpty() ? "/" : path;
    if (query != null){
      uri += "?" + query;
    }

    HttpFields fields = new HttpFields();
    fields.add(HttpHeader.ACCEPT, "text/gemini, */*");
    getRequest().setMetaData(new MetaData.Request("GET", schema, new HostPortHttpField(host, 1965), uri, HttpVersion.HTTP_1_1, fields, 0));
  }
}
