package kz.greetgo.mvc.annotations.on_methods;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determine format of request address for request performing by HTTP method OPTIONS
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnOptions {
  /**
   * @return format of request address
   */
  String[] value();
}
