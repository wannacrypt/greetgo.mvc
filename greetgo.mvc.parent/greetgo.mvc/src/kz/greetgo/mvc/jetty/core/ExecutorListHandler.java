package kz.greetgo.mvc.jetty.core;

import kz.greetgo.mvc.jetty.interfaces.RequestTunnel;
import kz.greetgo.mvc.jetty.interfaces.TunnelExecutor;
import kz.greetgo.mvc.jetty.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.jetty.interfaces.TunnelHandler;

import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.mvc.jetty.core.RequestMethod.POST;

public class ExecutorListHandler implements TunnelHandler {
  public static final String MULTIPART_FORM_DATA_TYPE = "multipart/form-data";

  public final List<TunnelExecutorGetter> tunnelExecutorGetters = new ArrayList<>();

  public static boolean isMultipart(String contentType) {
    return contentType != null && contentType.startsWith(MULTIPART_FORM_DATA_TYPE);
  }

  @Override
  public void handleTunnel(RequestTunnel tunnel) {
    final TunnelExecutor tunnelExecutor = getTunnelHandler(tunnel);
    if (tunnelExecutor == null) return;

    boolean multipartRequest = (tunnel.getRequestMethod() == POST) && isMultipart(tunnel.getRequestContentType());

    if (multipartRequest) {
      tunnel.enableMultipartSupport(tunnelExecutor.getUploadInfo());
    }

    try {
      tunnelExecutor.execute();
      tunnel.setExecuted(true);
    } finally {

      if (multipartRequest) {
        tunnel.removeMultipartData();
      }

    }
  }

  private TunnelExecutor getTunnelHandler(RequestTunnel tunnel) {
    for (TunnelExecutorGetter tunnelExecutorGetter : tunnelExecutorGetters) {
      final TunnelExecutor tunnelExecutor = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (tunnelExecutor != null) return tunnelExecutor;
    }
    return null;
  }
}
