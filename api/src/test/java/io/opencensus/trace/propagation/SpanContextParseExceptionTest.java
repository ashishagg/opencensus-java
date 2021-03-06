/*
 * Copyright 2017, OpenCensus Authors
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

package io.opencensus.trace.propagation;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link SpanContextParseException}. */
@RunWith(JUnit4.class)
public class SpanContextParseExceptionTest {

  @Test
  public void createWithMessage() {
    assertThat(new SpanContextParseException("my message").getMessage()).isEqualTo("my message");
  }

  @Test
  public void createWithMessageAndCause() {
    IOException cause = new IOException();
    SpanContextParseException parseException = new SpanContextParseException("my message", cause);
    assertThat(parseException.getMessage()).isEqualTo("my message");
    assertThat(parseException.getCause()).isEqualTo(cause);
  }
}
