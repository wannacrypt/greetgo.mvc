package kz.greetgo.mvc.war.stand;

import kz.greetgo.mvc.war.AppServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class App implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

    UserDetailsStorage userDetailsStorage = new UserDetailsStorage();

    final AppServlet appServlet = new StandAppServlet(userDetailsStorage);
    appServlet.register(ctx);

    String securityDir = ctx.getRealPath("");
    while (securityDir.endsWith("/")) securityDir = securityDir.substring(0, securityDir.length() - 1);
    securityDir += "_security";

    final StandSecurityFilter securityFilter = new StandSecurityFilter(securityDir, userDetailsStorage);
    securityFilter.register(ctx);
  }
}
