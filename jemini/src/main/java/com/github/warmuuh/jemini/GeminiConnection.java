package com.github.warmuuh.jemini;

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
import org.eclipse.jetty.server.SecureRequestCustomizer;
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
  private HttpConfiguration configuration;

  protected GeminiConnection(ByteBufferPool bufferPool, int bufferSize, Connector connector,
      EndPoint endp, Executor executor) {
    super(endp, executor);
    this.bufferPool = bufferPool;
    this.bufferSize = bufferSize;
    this.connector = connector;

    configuration = new HttpConfiguration();
    configuration.addCustomizer(new SecureRequestCustomizer());
    configuration.addCustomizer(new SessionIdByClientCertCustomizer());
    configuration.setRelativeRedirectAllowed(true);
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
        var adapter = new GeminiHttpChannelAdapter(connector, configuration, getEndPoint(), this);
        if (parser.handle(buffer, adapter)){
          adapter.handle();
        }
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
    GeminiStatus status = GeminiStatusHttpTranslation.mapFromHttp(info.getStatus());

    try{

      if (status.is2xSuccess()) {
        String mediaType = info.getFields().get(HttpHeader.CONTENT_TYPE);
        if (mediaType == null) {
          mediaType = "text/gemini; charset=utf-8";
        }
        writeBlocking(ByteBuffer.wrap((status.getStatus() + " " + mediaType + "\r\n").getBytes()));
        if (status.is2xSuccess()){
          writeBlocking(content);
        }
      } else if (status.is3xRedirect()){
        String newLocation = info.getFields().get(HttpHeader.LOCATION);
        writeBlocking(ByteBuffer.wrap((status + " " + newLocation + "\r\n").getBytes()));
      } else {
        String reason = info.getReason() != null ? info.getReason() : "";
        writeBlocking(ByteBuffer.wrap((status + " " + reason + "\r\n").getBytes()));
      }

      callback.succeeded();
    } catch (IOException e){
      callback.failed(e);
    } finally {
      getEndPoint().close();
    }
  }


  private void writeBlocking(ByteBuffer content) throws IOException {
    var cb = new SharedBlockingCallback();
      var blocker = cb.acquire();
      getEndPoint().write(blocker, content);
      blocker.block();
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
