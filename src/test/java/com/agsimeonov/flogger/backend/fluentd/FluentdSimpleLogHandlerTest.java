/*
 * Copyright (C) 2019 Alexander Simeonov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agsimeonov.flogger.backend.fluentd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FluentdSimpleLogHandlerTest {

  @ParameterizedTest
  @MethodSource("handlerComponentsProvider")
  void testHandleFormattedLevel(FluentdSimpleLogHandler handler, @SuppressWarnings("rawtypes") Map out) {
    handler.handleFormattedLogMessage(Level.CONFIG, null, null);
    assertEquals(out.get("level"), Level.CONFIG.getName());
  }

  @ParameterizedTest
  @MethodSource("handlerComponentsProvider")
  void testHandleFormattedMessage(FluentdSimpleLogHandler handler, @SuppressWarnings("rawtypes") Map out) {
    String message = "test";
    handler.handleFormattedLogMessage(null, message, null);
    assertEquals(out.get("message"), message);
  }

  @ParameterizedTest
  @MethodSource("handlerComponentsProvider")
  void testHandleFormattedMessageWithContext(FluentdSimpleLogHandler handler, @SuppressWarnings("rawtypes") Map out) {
    String message = "test [CONTEXT test]";
    handler.handleFormattedLogMessage(null, message, null);
    assertEquals(out.get("message"), "test");
  }

  @ParameterizedTest
  @MethodSource("handlerComponentsProvider")
  void testHandleFormattedContext(FluentdSimpleLogHandler handler, @SuppressWarnings("rawtypes") Map out) {
    String message = "[CONTEXT test]";
    handler.handleFormattedLogMessage(null, message, null);
    assertNull(out.get("message"));
  }

  @ParameterizedTest
  @MethodSource("handlerComponentsProvider")
  void testHandleFormattedThrown(FluentdSimpleLogHandler handler, @SuppressWarnings("rawtypes") Map out) throws IOException {
    Throwable thrown = new Exception();
    handler.handleFormattedLogMessage(null, null, thrown);
    assertEquals(out.get("thrown"), thrown);
    try (StringWriter stringWriter = new StringWriter()) {
      try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
        thrown.printStackTrace(printWriter);
        assertEquals(out.get("stackTrace"), stringWriter.toString());
      }
    } catch (IOException exception) {
      throw exception;
    }
  }

  static Stream<Arguments> handlerComponentsProvider() {
    Map<String, Object> out = new HashMap<>();
    FluentdSimpleLogHandler handler = new FluentdSimpleLogHandler(out);
    return Stream.of(Arguments.of(handler, out));
  }
}
