/*
 * Copyright 2018, OpenCensus Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opencensus.trace;

import static com.google.common.truth.Truth.assertThat;

import java.nio.CharBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link BigendianEncoding}. */
@RunWith(JUnit4.class)
public class BigendianEncodingTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  private static final long FIRST_LONG = 0x1213141516171819L;
  private static final byte[] FIRST_BYTE_ARRAY =
      new byte[] {0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19};
  private static final char[] FIRST_CHAR_ARRAY =
      new char[] {'1', '2', '1', '3', '1', '4', '1', '5', '1', '6', '1', '7', '1', '8', '1', '9'};
  private static final long SECOND_LONG = 0xFFEEDDCCBBAA9988L;
  private static final byte[] SECOND_BYTE_ARRAY =
      new byte[] {
        (byte) 0xFF, (byte) 0xEE, (byte) 0xDD, (byte) 0xCC,
        (byte) 0xBB, (byte) 0xAA, (byte) 0x99, (byte) 0x88
      };
  private static final char[] SECOND_CHAR_ARRAY =
      new char[] {'f', 'f', 'e', 'e', 'd', 'd', 'c', 'c', 'b', 'b', 'a', 'a', '9', '9', '8', '8'};
  private static final byte[] BOTH_BYTE_ARRAY =
      new byte[] {
        0x12,
        0x13,
        0x14,
        0x15,
        0x16,
        0x17,
        0x18,
        0x19,
        (byte) 0xFF,
        (byte) 0xEE,
        (byte) 0xDD,
        (byte) 0xCC,
        (byte) 0xBB,
        (byte) 0xAA,
        (byte) 0x99,
        (byte) 0x88
      };
  private static final char[] BOTH_CHAR_ARRAY =
      new char[] {
        '1', '2', '1', '3', '1', '4', '1', '5', '1', '6', '1', '7', '1', '8', '1', '9', 'f', 'f',
        'e', 'e', 'd', 'd', 'c', 'c', 'b', 'b', 'a', 'a', '9', '9', '8', '8'
      };

  @Test
  public void longToByteArray_Fails() {
    // These contain bytes not in the decoding.
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("array too small");
    BigendianEncoding.longToByteArray(123, new byte[BigendianEncoding.LONG_BYTES], 1);
  }

  @Test
  public void longToByteArray() {
    byte[] result1 = new byte[BigendianEncoding.LONG_BYTES];
    BigendianEncoding.longToByteArray(FIRST_LONG, result1, 0);
    assertThat(result1).isEqualTo(FIRST_BYTE_ARRAY);

    byte[] result2 = new byte[BigendianEncoding.LONG_BYTES];
    BigendianEncoding.longToByteArray(SECOND_LONG, result2, 0);
    assertThat(result2).isEqualTo(SECOND_BYTE_ARRAY);

    byte[] result3 = new byte[2 * BigendianEncoding.LONG_BYTES];
    BigendianEncoding.longToByteArray(FIRST_LONG, result3, 0);
    BigendianEncoding.longToByteArray(SECOND_LONG, result3, BigendianEncoding.LONG_BYTES);
    assertThat(result3).isEqualTo(BOTH_BYTE_ARRAY);
  }

  @Test
  public void longFromByteArray_ArrayToSmall() {
    // These contain bytes not in the decoding.
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("array too small");
    BigendianEncoding.longFromByteArray(new byte[BigendianEncoding.LONG_BYTES], 1);
  }

  @Test
  public void longFromByteArray() {
    assertThat(BigendianEncoding.longFromByteArray(FIRST_BYTE_ARRAY, 0)).isEqualTo(FIRST_LONG);

    assertThat(BigendianEncoding.longFromByteArray(SECOND_BYTE_ARRAY, 0)).isEqualTo(SECOND_LONG);

    assertThat(BigendianEncoding.longFromByteArray(BOTH_BYTE_ARRAY, 0)).isEqualTo(FIRST_LONG);

    assertThat(BigendianEncoding.longFromByteArray(BOTH_BYTE_ARRAY, BigendianEncoding.LONG_BYTES))
        .isEqualTo(SECOND_LONG);
  }

  @Test
  public void toFromByteArray() {
    toFromByteArrayValidate(0x8000000000000000L);
    toFromByteArrayValidate(-1);
    toFromByteArrayValidate(0);
    toFromByteArrayValidate(1);
    toFromByteArrayValidate(0x7FFFFFFFFFFFFFFFL);
  }

  @Test
  public void longToBase16String() {
    StringBuilder result1 = new StringBuilder(BigendianEncoding.LONG_BASE16);
    BigendianEncoding.longToBase16String(FIRST_LONG, result1);
    assertThat(result1.toString()).isEqualTo(new String(FIRST_CHAR_ARRAY));

    StringBuilder result2 = new StringBuilder(BigendianEncoding.LONG_BASE16);
    BigendianEncoding.longToBase16String(SECOND_LONG, result2);
    assertThat(result2.toString()).isEqualTo(new String(SECOND_CHAR_ARRAY));

    StringBuilder result3 = new StringBuilder(2 * BigendianEncoding.LONG_BASE16);
    BigendianEncoding.longToBase16String(FIRST_LONG, result3);
    BigendianEncoding.longToBase16String(SECOND_LONG, result3);
    assertThat(result3.toString()).isEqualTo(new String(BOTH_CHAR_ARRAY));
  }

  @Test
  public void longFromBase16String_InputTooSmall() {
    // Valid base16 strings always have an even length.
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("chars too small");
    BigendianEncoding.longFromBase16String(
        CharBuffer.wrap(new char[BigendianEncoding.LONG_BASE16]), 1);
  }

  @Test
  public void longFromBase16String_UnrecongnizedCharacters() {
    // These contain bytes not in the decoding.
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("invalid character g");
    BigendianEncoding.longFromBase16String("0123456789gbcdef", 0);
  }

  @Test
  public void longFromBase16String() {
    assertThat(BigendianEncoding.longFromBase16String(CharBuffer.wrap(FIRST_CHAR_ARRAY), 0))
        .isEqualTo(FIRST_LONG);

    assertThat(BigendianEncoding.longFromBase16String(CharBuffer.wrap(SECOND_CHAR_ARRAY), 0))
        .isEqualTo(SECOND_LONG);

    assertThat(BigendianEncoding.longFromBase16String(CharBuffer.wrap(BOTH_CHAR_ARRAY), 0))
        .isEqualTo(FIRST_LONG);

    assertThat(
            BigendianEncoding.longFromBase16String(
                CharBuffer.wrap(BOTH_CHAR_ARRAY), BigendianEncoding.LONG_BASE16))
        .isEqualTo(SECOND_LONG);
  }

  @Test
  public void toFromBase16String() {
    toFromBase16StringValidate(0x8000000000000000L);
    toFromBase16StringValidate(-1);
    toFromBase16StringValidate(0);
    toFromBase16StringValidate(1);
    toFromBase16StringValidate(0x7FFFFFFFFFFFFFFFL);
  }

  private static void toFromByteArrayValidate(long value) {
    byte[] array = new byte[BigendianEncoding.LONG_BYTES];
    BigendianEncoding.longToByteArray(value, array, 0);
    assertThat(BigendianEncoding.longFromByteArray(array, 0)).isEqualTo(value);
  }

  private static void toFromBase16StringValidate(long value) {
    StringBuilder dest = new StringBuilder(BigendianEncoding.LONG_BASE16);
    BigendianEncoding.longToBase16String(value, dest);
    assertThat(BigendianEncoding.longFromBase16String(dest.toString(), 0)).isEqualTo(value);
  }
}
