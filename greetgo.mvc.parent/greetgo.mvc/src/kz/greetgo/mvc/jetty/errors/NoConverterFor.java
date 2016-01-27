package kz.greetgo.mvc.jetty.errors;

public class NoConverterFor extends RuntimeException {
  public final Class<?> aClass;

  public NoConverterFor(Class<?> aClass) {
    super("No converter for " + aClass);
    this.aClass = aClass;
  }
}