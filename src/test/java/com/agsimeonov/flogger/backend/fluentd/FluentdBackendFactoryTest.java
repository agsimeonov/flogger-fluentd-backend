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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.commons.support.ReflectionSupport.findMethod;
import static org.junit.platform.commons.support.ReflectionSupport.invokeMethod;
import static org.junit.platform.commons.support.ReflectionSupport.newInstance;
import static org.junit.platform.commons.support.ReflectionSupport.tryToReadFieldValue;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.system.BackendFactory;
import com.google.common.flogger.backend.system.StackBasedCallerFinder;

import org.fluentd.logger.Config;
import org.fluentd.logger.FluentLogger;
import org.fluentd.logger.sender.RawSocketSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class FluentdBackendFactoryTest {

  static BackendFactory backendFactory;

  @BeforeAll
  static void initialize() {
    System.setProperty("flogger.backend_factory", "com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory#getInstance");
    System.setProperty("flogger.caller_finder", "com.agsimeonov.flogger.backend.fluentd.SystemPropertiesCallerFinder#getInstance");
    System.setProperty("flogger.tag_prefix", "test");
    backendFactory = newInstance(FluentdBackendFactory.class);
  }

  @Test
  void testGetInstance() {
    assertNotNull(FluentdBackendFactory.getInstance());
  }

  @Test
  void testInjectCallerFinder() {
    assertEquals(StackBasedCallerFinder.getInstance(), SystemPropertiesCallerFinder.getInstance());
  }

  @ParameterizedTest
  @MethodSource("stringProvider")
  void testCreate(String loggingClassName) {
    assertNotNull(backendFactory.create(loggingClassName));
  }

  @ParameterizedTest
  @MethodSource("stringProvider")
  void testCreateCached(String loggingClassName) {
    assertEquals(backendFactory.create(loggingClassName), backendFactory.create(loggingClassName));
    assertNotEquals(backendFactory.create(loggingClassName), backendFactory.create(stringProvider().findFirst().get()));
  }

  @Test
  @ResourceLock(Resources.SYSTEM_PROPERTIES)
  void testCreateWithRemoteSettings() throws Exception {
    System.setProperty(Config.FLUENT_SENDER_CLASS, RawSocketSender.class.getName());
    LoggerBackend backend = backendFactory.create(stringProvider().findFirst().get());
    FluentLogger logger = tryToReadFieldValue(backend.getClass().getDeclaredField("logger"), backend).andThenTry(x -> (FluentLogger) x).get();
    assertEquals(tryToReadFieldValue(RawSocketSender.class.getDeclaredField("host"), logger.getSender()).get(), "localhost");
    assertEquals(tryToReadFieldValue(RawSocketSender.class.getDeclaredField("port"), logger.getSender()).get(), 24224);
    System.setProperty("flogger.remote_settings", "com.agsimeonov.flogger.backend.fluentd.SystemPropertiesRemoteSettings#getInstance");
    String host = stringProvider().findFirst().get();
    Integer port = 12345;
    System.setProperty("flogger.fluentd_host", host);
    System.setProperty("flogger.fluentd_port", port.toString());
    backend = backendFactory.create(stringProvider().findFirst().get());
    logger = tryToReadFieldValue(backend.getClass().getDeclaredField("logger"), backend).andThenTry(x -> (FluentLogger) x).get();
    assertEquals(tryToReadFieldValue(RawSocketSender.class.getDeclaredField("host"), logger.getSender()).get(), host);
    assertEquals(tryToReadFieldValue(RawSocketSender.class.getDeclaredField("port"), logger.getSender()).get(), port);
  }

  @Test
  void testCreateWithLevelDisabler() throws Exception {
    LoggerBackend backend = backendFactory.create(stringProvider().findFirst().get());
    FluentdLevelDisabler disabler = tryToReadFieldValue(backend.getClass().getDeclaredField("disabler"), backend).andThenTry(x -> (FluentdLevelDisabler) x).get();
    assertNull(disabler);
    System.setProperty("flogger.level_disabler", "com.agsimeonov.flogger.backend.fluentd.SystemPropertiesLevelDisabler#getInstance");
    backend = backendFactory.create(stringProvider().findFirst().get());
    disabler = tryToReadFieldValue(backend.getClass().getDeclaredField("disabler"), backend).andThenTry(x -> (FluentdLevelDisabler) x).get();
    assertEquals(disabler, SystemPropertiesLevelDisabler.getInstance());
  }

  @ParameterizedTest
  @CsvSource("caller_finder, com.agsimeonov.flogger.backend.fluentd.SystemPropertiesCallerFinder")
  void testResolveAttribute(String attributeName, @SuppressWarnings("rawtypes") Class type) {
    Optional<Method> method = findMethod(FluentdBackendFactory.class, "resolveAttribute", attributeName.getClass(), type.getClass());
    assertTrue(method.isPresent());
    assertEquals(invokeMethod(method.get(), null, attributeName, type), SystemPropertiesCallerFinder.getInstance());
  }

  static Stream<String> stringProvider() {
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    int length = 30;
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) builder.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
    return Stream.of(builder.toString());
  }
}
