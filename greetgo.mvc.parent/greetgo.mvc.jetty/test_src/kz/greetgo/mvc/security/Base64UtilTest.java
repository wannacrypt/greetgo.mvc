package kz.greetgo.mvc.security;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class Base64UtilTest {

  @Test
  public void bytesToBase64_base64ToBytes() throws Exception {
    final byte[] bytes = RND.byteArray(123);

    //
    //
    final String base64str = Base64Util.bytesToBase64(bytes);
    //
    //

    //
    //
    final byte[] bytesActual = Base64Util.base64ToBytes(base64str);
    //
    //

    assertThat(bytesActual).isEqualTo(bytes);
  }

  @Test
  public void base64ToBytes_left() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes("Левая строка");
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void base64ToBytes_left2() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes("       ");
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void base64ToBytes_empty() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes("");
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void base64ToBytes_null() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes(null);
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void bytesToBase64_null() throws Exception {
    //
    //
    final String str = Base64Util.bytesToBase64(null);
    //
    //

    assertThat(str).isNull();
  }

}