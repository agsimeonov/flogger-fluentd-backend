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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FluentdTagsKeyValueHandlerTest {

  @ParameterizedTest
  @MethodSource("handlerComponentsProvider")
  void testHandle(FluentdTagsKeyValueHandler handler, String label, String key, String value, @SuppressWarnings("rawtypes") Map out) {
    handler.handle(key, value);
    assertEquals(out.get(key), value);
  }

  @ParameterizedTest
  @MethodSource("handlerComponentsProvider")
  @SuppressWarnings("unchecked")
  void testHandleNullValue(FluentdTagsKeyValueHandler handler, String label, String key, String value, @SuppressWarnings("rawtypes") Map out) {
    handler.handle(key, null);
    assertTrue(out.get(label) instanceof List);
    assertEquals(((List<String>) out.get(label)).get(0), key);
  }

  static Stream<Arguments> handlerComponentsProvider() {
    String label = FluentdBackendFactoryTest.stringProvider().findFirst().get();
    String key = FluentdBackendFactoryTest.stringProvider().findFirst().get();
    String value = FluentdBackendFactoryTest.stringProvider().findFirst().get();
    Map<String, Object> out = new HashMap<>();
    FluentdTagsKeyValueHandler handler = new FluentdTagsKeyValueHandler(label, out);
    return Stream.of(Arguments.of(handler, label, key, value, out));
  }
}
