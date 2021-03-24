package wrm;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MetaData.Request;
import org.eclipse.jetty.http.MetaData.Response;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpTransport;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.SharedBlockingCallback;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class GeminiConnection extends AbstractConnection implements HttpTransport {

  private static final Logger LOG = Log.getLogger(GeminiConnection.class);


  private final ByteBufferPool bufferPool;
  private final int bufferSize;
  private final Connector connector;
  private GeminiProtocolParser parser = new GeminiProtocolParser();

  protected GeminiConnection(ByteBufferPool bufferPool, int bufferSize, Connector connector,
      EndPoint endp, Executor executor) {
    super(endp, executor);
    this.bufferPool = bufferPool;
    this.bufferSize = bufferSize;
    this.connector = connector;
  }

  @Override
  public void onOpen() {
    super.onOpen();
    fillInterested();
  }

  @Override
  public void onFillable() {
    ByteBuffer buffer = bufferPool.acquire(bufferSize, false);
    boolean readMore = read(buffer) == 0;
    bufferPool.release(buffer);
    if (readMore) {
      fillInterested();
    }
  }

  protected int read(ByteBuffer buffer) {
    EndPoint endPoint = getEndPoint();
    while (true) {
      int filled = fill(endPoint, buffer);
      if (LOG.isDebugEnabled()) // Avoid boxing of variable 'filled'
      {
        LOG.debug("Read {} bytes", filled);
      }
      if (filled == 0) {
        return 0;
      } else if (filled < 0) {
//        shutdown(session);
        return -1;
      } else {
        var adapter = new GeminiHttpChannelAdapter(connector, new HttpConfiguration(), getEndPoint(), this);
        parser.handle(buffer, adapter);
        adapter.handle();
      }
    }
  }

  private int fill(EndPoint endPoint, ByteBuffer buffer) {
    try {
      if (endPoint.isInputShutdown()) {
        return -1;
      }
      return endPoint.fill(buffer);
    } catch (IOException x) {
      endPoint.close();
      throw new RuntimeIOException(x);
    }
  }


  @Override
  public void send(Response info, boolean head, ByteBuffer content, boolean lastContent, Callback callback) {
    String mediaType = info.getFields().get(HttpHeader.CONTENT_TYPE);
    if (mediaType == null) {
      mediaType = "text/gemini; charset=utf-8";
    }
    int status = mapFromHttp(info.getStatus());

    writeBlocking(ByteBuffer.wrap((status + " " + mediaType + "\r\n").getBytes()));
    writeBlocking(content);
    getEndPoint().close();
  }

  private int mapFromHttp(int status) {
    if (status >= 200 && status < 299) {
      return 20;
    }
    if (status >= 300 && status < 399){
      return switch (status) {
        case 302, 307 -> 30;
        case 301, 308 -> 31;
        default -> 30;
      };
    }
    if (status >= 400 && status < 499) {
      return switch (status) {
        case 429 -> 44;
        case 404 -> 51;
        case 410 -> 52;
        case 400 -> 59;
        default -> 40;
      };
    }

    if (status >= 500 && status < 599) {
      return switch (status) {
        case 502, 504 -> 43;
        case 503 -> 41;
        default -> 50;
      };
    }

    return 20;
  }

  private void writeBlocking(ByteBuffer content) {
    var cb = new SharedBlockingCallback();
    try {
      var blocker = cb.acquire();
      getEndPoint().write(blocker, content);
      blocker.block();

    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  @Override
  public boolean isPushSupported() {
    return false;
  }

  @Override
  public void push(Request request) {

  }

  @Override
  public void onCompleted() {
    getEndPoint().close();
  }

  @Override
  public void abort(Throwable failure) {

  }

  @Override
  public boolean isOptimizedForDirectBuffers() {
    return false;
  }
}
