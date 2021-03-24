package wrm;

import java.net.URI;
import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpParser.RequestHandler;

public class GeminiProtocolParser {
  private final StringBuilder urlBuffer = new StringBuilder(1024);


  
  public boolean handle(ByteBuffer buffer, GeminiParserListener adapter) {
    var uri = handlePath(buffer);
    if (uri == null) {
      return false;
    }
    adapter.startRequest(uri.getScheme(), uri.getHost(), uri.getPath(), uri.getQuery());

    return true;
  }

  private URI handlePath(ByteBuffer buffer) {
    while (buffer.hasRemaining()) {
      final int curPos = buffer.position();
      if (buffer.remaining() > 2
          && buffer.get(curPos) == '\r'
          && buffer.get(curPos + 1) == '\n'){
        return URI.create(urlBuffer.toString());
      }
      var next = (char) (buffer.get() & 0xFF);
      urlBuffer.append(next);
      if (urlBuffer.toString().endsWith("\r\n")){
        return URI.create(urlBuffer.toString().trim());
      }
    }

    return null;
  }

  public interface GeminiParserListener {
    void startRequest(String scheme, String host, String path, String query);
  }

}
