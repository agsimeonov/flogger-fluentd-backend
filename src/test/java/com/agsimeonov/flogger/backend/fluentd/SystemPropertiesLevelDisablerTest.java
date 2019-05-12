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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;

import java.util.logging.Level;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

class SystemPropertiesLevelDisablerTest {

  @Test
  void testGetInstance() {
    assertNotNull(SystemPropertiesLevelDisabler.getInstance());
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testModeNotSet() {
    System.clearProperty(SystemPropertiesLevelDisabler.EXCLUSIVE);
    assertTrue(SystemPropertiesLevelDisabler.getInstance().isLoggable(null));
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testExclusive() {
    System.setProperty(SystemPropertiesLevelDisabler.EXCLUSIVE, "true");
    String namePropertyKey = SystemPropertiesLevelDisabler.getNamePropertyKey(Level.CONFIG);
    System.clearProperty(namePropertyKey);
    System.clearProperty(SystemPropertiesLevelDisabler.LEVEL);
    assertTrue(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.CONFIG));
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testExclusiveName() {
    System.setProperty(SystemPropertiesLevelDisabler.EXCLUSIVE, "true");
    System.setProperty(SystemPropertiesLevelDisabler.getNamePropertyKey(Level.CONFIG), "true");
    System.clearProperty(SystemPropertiesLevelDisabler.LEVEL);
    assertFalse(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.CONFIG));
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testExclusiveLevel() {
    System.setProperty(SystemPropertiesLevelDisabler.EXCLUSIVE, "true");
    System.clearProperty(SystemPropertiesLevelDisabler.getNamePropertyKey(Level.CONFIG));
    System.setProperty(SystemPropertiesLevelDisabler.LEVEL, Integer.valueOf(Level.CONFIG.intValue()).toString());
    assertFalse(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.CONFIG));
    assertFalse(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.FINEST));
    assertTrue(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.SEVERE));
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testInclusive() {
    System.setProperty(SystemPropertiesLevelDisabler.EXCLUSIVE, "false");
    String namePropertyKey = SystemPropertiesLevelDisabler.getNamePropertyKey(Level.CONFIG);
    System.clearProperty(namePropertyKey);
    System.clearProperty(SystemPropertiesLevelDisabler.LEVEL);
    assertFalse(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.CONFIG));
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testInclusiveName() {
    System.setProperty(SystemPropertiesLevelDisabler.EXCLUSIVE, "false");
    System.setProperty(SystemPropertiesLevelDisabler.getNamePropertyKey(Level.CONFIG), "true");
    System.clearProperty(SystemPropertiesLevelDisabler.LEVEL);
    assertTrue(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.CONFIG));
  }

  @Test
  @ResourceLock(SYSTEM_PROPERTIES)
  void testInclusiveLevel() {
    System.setProperty(SystemPropertiesLevelDisabler.EXCLUSIVE, "false");
    System.clearProperty(SystemPropertiesLevelDisabler.getNamePropertyKey(Level.CONFIG));
    System.setProperty(SystemPropertiesLevelDisabler.LEVEL, Integer.valueOf(Level.CONFIG.intValue()).toString());
    assertTrue(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.CONFIG));
    assertFalse(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.FINEST));
    assertTrue(SystemPropertiesLevelDisabler.getInstance().isLoggable(Level.SEVERE));
  }
}
