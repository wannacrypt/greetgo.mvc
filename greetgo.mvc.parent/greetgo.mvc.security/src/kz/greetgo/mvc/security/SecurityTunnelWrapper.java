package kz.greetgo.mvc.security;

import kz.greetgo.mvc.jetty.interfaces.RequestTunnel;
import kz.greetgo.mvc.jetty.interfaces.TunnelHandler;
import kz.greetgo.util.events.EventHandler;
import kz.greetgo.util.events.HandlerKiller;

import java.util.Objects;

import static kz.greetgo.mvc.security.Base64Util.base64ToBytes;
import static kz.greetgo.mvc.security.Base64Util.bytesToBase64;

public class SecurityTunnelWrapper implements TunnelHandler {

  private final TunnelHandler whatWrapping;
  private final SecurityProvider provider;
  private final SessionStorage sessionStorage;
  private final SecurityCrypto sessionCrypto;
  private final SecurityCrypto signatureCrypto;

  public SecurityTunnelWrapper(TunnelHandler whatWrapping,
                               SecurityProvider provider,
                               SessionStorage sessionStorage,
                               SecurityCrypto sessionCrypto,
                               SecurityCrypto signatureCrypto) {
    this.whatWrapping = whatWrapping;
    this.provider = provider;
    this.sessionStorage = sessionStorage;
    this.sessionCrypto = sessionCrypto;
    this.signatureCrypto = signatureCrypto;
  }

  @Override
  public void handleTunnel(final RequestTunnel tunnel) {

    final boolean underSecurityUmbrella = provider.isUnderSecurityUmbrella(tunnel.getTarget());

    final byte[] bytesInStorage;

    {
      final String sessionBase64 = tunnel.cookies().getRequestCookieValue(provider.cookieKeySession());
      byte[] bytes = base64ToBytes(sessionBase64);

      if (sessionCrypto != null) {
        bytes = sessionCrypto.decrypt(bytes);
      }

      if (underSecurityUmbrella && bytes != null && signatureCrypto != null) {
        String signatureBase64 = tunnel.cookies().getRequestCookieValue(provider.cookieKeySignature());
        byte[] signature = base64ToBytes(signatureBase64);
        if (!signatureCrypto.verifySignature(bytes, signature)) {
          bytes = null;
        }
      }

      sessionStorage.setSessionBytes(bytesInStorage = bytes);
    }

    final EventHandler writeSessionToCookies = new EventHandler() {
      boolean performed = false;

      @Override
      public void handle() {
        if (performed) return;
        performed = true;

        byte[] bytes = sessionStorage.getSessionBytes();
        if (!Objects.equals(bytesInStorage, bytes)) {

          if (bytes == null) {
            tunnel.cookies().removeCookieFromResponse(provider.cookieKeySession());
            tunnel.cookies().removeCookieFromResponse(provider.cookieKeySignature());
          } else {

            if (signatureCrypto != null) {
              byte[] signature = signatureCrypto.sign(bytes);
              final String signatureBase64 = bytesToBase64(signature);
              tunnel.cookies().saveCookieToResponse(provider.cookieKeySignature(), signatureBase64);
            }

            if (sessionCrypto != null) {
              bytes = sessionCrypto.encrypt(bytes);
            }

            final String bytesBase64 = bytesToBase64(bytes);
            tunnel.cookies().saveCookieToResponse(provider.cookieKeySession(), bytesBase64);

          }

        }
      }
    };

    final HandlerKiller handlerKiller = tunnel.eventBeforeCompleteHeaders().addEventHandler(writeSessionToCookies);

    try {

      if (!underSecurityUmbrella) {

        whatWrapping.handleTunnel(tunnel);

      } else {

        if (sessionStorage.getSessionBytes() == null) {
          tunnel.sendRedirect(provider.redirectOnSecurityError(tunnel.getTarget()));
        } else {
          whatWrapping.handleTunnel(tunnel);
        }

      }

    } finally {
      handlerKiller.kill();
      writeSessionToCookies.handle();
    }

  }
}