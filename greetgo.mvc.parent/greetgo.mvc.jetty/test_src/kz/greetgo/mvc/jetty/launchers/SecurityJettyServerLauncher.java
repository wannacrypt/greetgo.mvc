package kz.greetgo.mvc.jetty.launchers;

import kz.greetgo.mvc.jetty.JettyWrapperOfTunnelHandler;
import kz.greetgo.mvc.jetty.TunnelHandlerWrapperOfJetty;
import kz.greetgo.mvc.jetty.controllers.LoginController;
import kz.greetgo.mvc.jetty.core.ControllerTunnelExecutorBuilder;
import kz.greetgo.mvc.jetty.core.ExecutorListHandler;
import kz.greetgo.mvc.jetty.core.TunnelHandlerList;
import kz.greetgo.mvc.jetty.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.jetty.utils.ProbeViews;
import kz.greetgo.mvc.jetty.utils.UserDetailsStorage;
import kz.greetgo.mvc.security.SecurityProvider;
import kz.greetgo.mvc.security.SecurityTunnelWrapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;
import java.util.List;

public class SecurityJettyServerLauncher {

  public static void main(String[] args) throws Exception {
    new SecurityJettyServerLauncher().run();
  }

  public SecurityJettyServerLauncher() {
  }


  private void run() throws Exception {

    TunnelHandlerList tunnelHandlerList = new TunnelHandlerList();

    {
      String warDir = "test_war_security";
      {
        String prj = "greetgo.mvc.jetty/";
        if (new File(prj).isDirectory()) {
          warDir = prj + warDir;
        }
      }

      ResourceHandler resourceHandler = new ResourceHandler();
      resourceHandler.setDirectoriesListed(true);
      resourceHandler.setWelcomeFiles(new String[]{"index.html"});
      resourceHandler.setResourceBase(warDir);

      tunnelHandlerList.list.add(new TunnelHandlerWrapperOfJetty(resourceHandler));

    }

    UserDetailsStorage userDetailsStorage = new UserDetailsStorage();

    {
      final ProbeViews views = new ProbeViews();
      LoginController loginController = new LoginController(userDetailsStorage);
      final List<TunnelExecutorGetter> executorList = ControllerTunnelExecutorBuilder.build(loginController, views);
      tunnelHandlerList.list.add(new ExecutorListHandler(executorList));
    }

    {
      Server server = new Server(8080);

      server.setHandler(new JettyWrapperOfTunnelHandler(new SecurityTunnelWrapper(
        tunnelHandlerList, securityProvider, userDetailsStorage, null, null)));

      server.start();
      server.join();
    }
  }

  private final SecurityProvider securityProvider = new SecurityProvider() {
    @Override
    public String cookieKeySession() {
      return "GGS";
    }

    @Override
    public String cookieKeySignature() {
      return "GGC";
    }

    @Override
    public boolean isUnderSecurityUmbrella(String target) {
      if (target.startsWith("/login")) return false;
      //noinspection RedundantIfStatement
      if (target.startsWith("/img/")) return false;
      return true;
    }

    @Override
    public String redirectOnSecurityError(String target) {
      return "/login.html";
    }
  };
}