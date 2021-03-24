package wrm;

import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.MetaData.Request;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpTransport;
import wrm.GeminiProtocolParser.GeminiParserListener;

public class GeminiHttpChannelAdapter extends HttpChannel implements GeminiParserListener {

  public GeminiHttpChannelAdapter(Connector connector,
      HttpConfiguration configuration, EndPoint endPoint,
      HttpTransport transport) {
    super(connector, configuration, endPoint, transport);
  }


  @Override
  public void startRequest(String schema, String host, String uri, String query) {
    getRequest().setScheme(schema);
    getRequest().setServletPath(uri);
    getRequest().setQueryString(query);

    getRequest().setMetaData(new MetaData.Request("GET", schema, new HostPortHttpField(host, 1965), uri, HttpVersion.HTTP_1_1, new HttpFields(), 0));
  }
}
