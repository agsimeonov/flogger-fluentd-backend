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

import java.util.logging.Level;

/** 
 * Determines whether logging is disabled at specific levels based on system properties.
 *
 * <p>To configure this level disabler for Flogger set the following system properties (also see {@link
 * com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory}):
 *
 * <ul>
 *   <li>{@code flogger.level_disabler=com.agsimeonov.flogger.backend.fluentd.SystemPropertiesLevelDisabler#getInstance}.
 *   <li>{@code flogger.exclusive=<true/false>}.
 *   <li>{@code flogger.<name>=<true/false>}.
 *   <li>{@code flogger.level=<integer>}.
 * </ul>
 */
public class SystemPropertiesLevelDisabler implements FluentdLevelDisabler {

  static final String EXCLUSIVE = "flogger.exclusive";
  static final String LEVEL = "flogger.level";

  private static final FluentdLevelDisabler INSTANCE = new SystemPropertiesLevelDisabler();

  /** Determines whether logging is disabled at specific levels based on system properties. */
  private SystemPropertiesLevelDisabler() {}

  /**
   * Acquires a singleton SystemPropertiesLoggingDisabler.
   *
   * @return the SystemPropertiesLoggingDisabler singleton.
   */
  public static FluentdLevelDisabler getInstance() {
    return INSTANCE;
  }

  /**
   * Determines whether given levels are loggable via the following system properties:
   * 
   * <p>This outcome method is configured using the following system properties:
   *
   * <ul>
   *   <li>{@code flogger.exclusive=<true/false>}.
   *   <li>{@code logger.<name>=<true/false>}.
   *   <li>{@code flogger.level=<integer>}
   * </ul><br>
   *
   * If flogger.exclusive is not set returns true.<br>
   * If flogger.exclusive is set to true and logger.&lt;name&gt; is set to true returns false.<br>
   * If flogger.exclusive is set to true and flogger.level is set returns true if parameter level is greater than flogger.level.<br>
   * Otherwise if flogger.exclusive is set to true returns true.<br>
   * If flogger.exclusive is set to false and logger.&lt;name&gt; is set to true returns true.<br>
   * If flogger.exclusive is set to false and flogger.level is set returns true if parameter level is less greater than or equal to flogger.level.<br>
   * Otherwise if flogger.exclusive is is to false returns false.<br>
   *
   * @param level the given level.
   * @return true if the given level is loggable, otherwise false.
   */
  @Override
  public boolean isLoggable(Level level) {
    String exclusive = System.getProperty(EXCLUSIVE);
    if (exclusive == null) return true;
    boolean isExclusive = exclusive.toLowerCase().equals("true") ? true : false;

    String name = System.getProperty(getNamePropertyKey(level));
    boolean isNameSet = name == null
                      ? false
                      : name.toLowerCase().equals("true") ? true : false;

    Integer value;
    try {
      value = Integer.valueOf(System.getProperty(LEVEL));
    } catch (Exception exception) {
      value = null;
    }
    
    if (isExclusive) {
      if (isNameSet) return false;
      if (value != null) return level.intValue() > value;
      return true;
    } else {
      if (isNameSet) return true;
      if (value != null) return level.intValue() >= value;
      return false;
    }
  }

  /**
   * Acquires the flogger.<name> system property key.
   *
   * @return the flogger.<name> system property key.
   */
  static String getNamePropertyKey(Level level) {
    return "flogger." + level.getName();
  }
}
