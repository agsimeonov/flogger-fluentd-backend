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
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.platform.commons.util.ReflectionUtils;

@ResourceLock(SYSTEM_PROPERTIES)
class SystemPropertiesCallerFinderTest {

  static String tagPrefix;
  static FluentdCallerFinder callerFinder;

  @BeforeAll
  static void initialize() {
    tagPrefix = FluentdBackendFactoryTest.stringProvider().findFirst().get();
    System.setProperty("flogger.tag_prefix", tagPrefix);
    callerFinder = ReflectionUtils.newInstance(SystemPropertiesCallerFinder.class);
  }

  @Test
  public void testFindLoggingClass() {
    assertEquals(callerFinder.findLoggingClass(null), tagPrefix);
  }
}
