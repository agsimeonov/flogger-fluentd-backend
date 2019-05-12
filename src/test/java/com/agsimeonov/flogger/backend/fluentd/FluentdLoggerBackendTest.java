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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;
import static org.junit.platform.commons.support.ReflectionSupport.tryToReadFieldValue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Stream;

import com.google.common.flogger.MetadataKey;
import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.Tags;
import com.google.common.flogger.backend.system.SimpleLogRecord;
import com.google.common.flogger.testing.FakeLogData;

import org.fluentd.logger.Config;
import org.fluentd.logger.FluentLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.moznion.fluent.logger.mock.sender.MockSender;

@ResourceLock(SYSTEM_PROPERTIES)
class FluentdLoggerBackendTest {

  static final String MOCK_SENDER = "MOCK_SENDER";

  static LoggerBackend backend;
  static MockSender sender;

  @BeforeAll
  static void initialize() throws Exception {
    System.setProperty(Config.FLUENT_SENDER_CLASS, MockSender.class.getName());
    backend = new FluentdLoggerBackend(FluentLogger.getLogger(FluentdLoggerBackendTest.class.getSimpleName()), SystemPropertiesLevelDisabler.getInstance());
    sender = tryToReadFieldValue(backend.getClass().getDeclaredField("logger"), backend)
      .andThenTry(x -> (FluentLogger) x)
      .andThenTry(logger -> (MockSender) logger.getSender())
      .get();
  }

  @Test
  public void testGetLoggerName() {
    assertTrue(backend.getLoggerName().startsWith(FluentdLoggerBackendTest.class.getSimpleName()));
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  public void testIsLoggable() {
    assertTrue(backend.isLoggable(Level.FINEST));
    System.setProperty("flogger.exclusive", "true");
    System.setProperty(SystemPropertiesLevelDisabler.getNamePropertyKey(Level.FINEST), "true");
    assertFalse(backend.isLoggable(Level.FINEST));
    System.setProperty("flogger.exclusive", "false");
    System.setProperty(SystemPropertiesLevelDisabler.getNamePropertyKey(Level.FINEST), "false");
  }

  @Test
  @ResourceLock(MOCK_SENDER)
  @SuppressWarnings("unchecked")
  public void testLog() {
    Date date = new Date(0L);
    long timestampNanos = TimeUnit.MILLISECONDS.convert(date.getTime(), TimeUnit.NANOSECONDS);
    LogData data = FakeLogData.withPrintfStyle("Hello %s %s", "Foo", "Bar")
      .setLevel(Level.CONFIG)
      .addMetadata(MetadataKey.single("tags", Tags.class), Tags.builder().addTag("a").addTag("b").addTag("c").addTag("d", 1L).build())
      .addMetadata(MetadataKey.single("test", String.class), "test")
      .addMetadata(MetadataKey.repeated("integers", Integer.class), 2)
      .addMetadata(MetadataKey.repeated("integers", Integer.class), 3)
      .setTimestampNanos(timestampNanos);
    backend.log(data);
    assertTrue(sender.getFluentLogs().size() == 1);
    Map<String, Object> log = sender.getFluentLogs().get(0).getData();
    assertEquals(log.get("message"), "Hello Foo Bar");
    assertEquals(log.get("fileName"), "src/com/google/FakeClass.java");
    assertEquals(log.get("methodName"), "fakeMethod");
    assertEquals(log.get("className"), "com.google.FakeClass");
    assertEquals(log.get("methodName"), "fakeMethod");
    assertEquals(log.get("timestampNanos"), timestampNanos);
    assertEquals(log.get("level"), Level.CONFIG.getName());
    assertEquals(log.get("test"), "test");
    List<String> tags = (List<String>) log.get("tags");
    assertTrue(Stream.of("a", "b", "c").allMatch(x -> tags.contains(x)));
    assertEquals(log.get("d"), 1L);
    List<Integer> integers = (List<Integer>) log.get("integers");
    assertTrue(Stream.of(2, 3).allMatch(x -> integers.contains(x)));
    sender.clearFluentLogs();
  }

  @ParameterizedTest
  @CsvSource("java.lang.RuntimeException, test")
  @ResourceLock(MOCK_SENDER)
  public void testHandleError(RuntimeException expected, String message) {
    LogData data = FakeLogData.of("message");
    backend.handleError(expected, data);
    assertTrue(sender.getFluentLogs().size() == 1);
    Map<String, Object> log = sender.getFluentLogs().get(0).getData();
    assertEquals(log.get("level"), Level.SEVERE.getName());
    assertEquals(log.get("message").toString(), SimpleLogRecord.error(expected, data).toString());
    sender.clearFluentLogs();
  }
}
