package kz.greetgo.mvc.jetty;

public interface TunnelHandler {
  void handle();

  MultipartConf getMultipartConf();
}
