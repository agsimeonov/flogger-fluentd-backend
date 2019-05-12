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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SystemPropertiesRemoteSettingsTest {

  @Test
  void testGetInstance() {
    assertNotNull(SystemPropertiesRemoteSettings.getInstance());
  }

  @ParameterizedTest
  @MethodSource("com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactoryTest#stringProvider")
  @ResourceLock(SYSTEM_PROPERTIES)
  void testGetHost(String host) {
    System.setProperty(SystemPropertiesRemoteSettings.FLUENTD_HOST, host);
    assertEquals(SystemPropertiesRemoteSettings.getInstance().getHost(), host);
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testGetHostFallback() {
    System.clearProperty(SystemPropertiesRemoteSettings.FLUENTD_HOST);
    assertEquals(SystemPropertiesRemoteSettings.getInstance().getHost(), "localhost");
  }

  @ParameterizedTest
  @ValueSource(ints = Integer.MIN_VALUE)
  @ResourceLock(SYSTEM_PROPERTIES)
  void testGetPort(Integer port) {
    System.setProperty(SystemPropertiesRemoteSettings.FLUENTD_PORT, port.toString());
    assertEquals(SystemPropertiesRemoteSettings.getInstance().getPort(), port);
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testGetPortFallback() {
    System.clearProperty(SystemPropertiesRemoteSettings.FLUENTD_PORT);
    assertEquals(SystemPropertiesRemoteSettings.getInstance().getPort(), 24224);
  }
}
