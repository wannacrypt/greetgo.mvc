package kz.greetgo.mvc.security;

/**
 * Настраивает секурити
 */
public interface SecurityProvider {
  /**
   * Указывает имя параметра cookie, в котором будет хранится сессия
   *
   * @return имя параметра cookie, в котором хранится сессия
   */
  String cookieKeySession();

  /**
   * Указывает имя параметра cookie, в котором будет хранится подпись сессии
   *
   * @return имя параметра cookie, в котором хранится подпись сессии
   */
  String cookieKeySignature();

  /**
   * Указывает таргеты. которые находятся под зонтиком безопасности
   *
   * @param target текущий таргет
   * @return необходимость в безопасности для данного таргета: <code>true</code> - безопасность будет предоставлена
   * (проверена и подготовлена сессия и пр.); <code>false</code> - проверка сессии производиться не будет, а решение
   * о подготовки сессии будет принято, вызовом метода {@link #skipSession(String)}
   */
  boolean isUnderSecurityUmbrella(String target);

  /**
   * Возвращает таргет-редирект, на который будет переправлена система, в случае, если произошло какое-либо
   * нарушение секурити
   *
   * @param target текущий таргет (на котором произошло нарушение секурити)
   * @return таргет, на который будет сделан редирект
   */
  String redirectOnSecurityError(String target);

  /**
   * Указывает таргеты, для которых сессию подготавливать не нужно (например для картинок, или статических css-файлов)
   *
   * @param target таргет
   * @return Если вернёт <code>true</code>, то сессия подготавливаться не будет, иначе - сессия будет подготовлена
   */
  boolean skipSession(String target);
}
