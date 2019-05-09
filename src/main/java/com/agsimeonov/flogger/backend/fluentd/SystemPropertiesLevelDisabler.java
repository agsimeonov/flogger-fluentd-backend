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
 * <p>To configure this level disabler for Flogger set the following system property (also see {@link
 * com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory}:
 *
 * <ul>
 *   <li>{@code flogger.level_disabler=com.agsimeonov.flogger.backend.fluentd.SystemPropertiesLevelDisabler#getInstance}.
 *   <li>{@code flogger.exclusive=&lt;anything&gt;}.
 *   <li>{@code flogger.inclusive=&lt;anything&gt;}.
 *   <li>{@code logger.&lt;name&gt;=&lt;anything&gt;}.
 *   <li>{@code flogger.level=&lt;integer&gt;}.
 * </ul>
 */
public class SystemPropertiesLevelDisabler implements FluentdLevelDisabler {

  private static final String EXCLUSIVE = "flogger.exclusive";
  private static final String INCLUSIVE = "flogger.inclusive";
  private static final String LEVEL = "flogger.level";

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
   * <ul>
   *   <li>{@code flogger.exclusive=&lt;anything&gt;}.
   *   <li>{@code flogger.inclusive=&lt;anything&gt;}.
   *   <li>{@code logger.&lt;name&gt;=&lt;anything&gt;}.
   *   <li>{@code flogger.level=&lt;integer&gt;}
   * </ul>
   * 
   * <pre>
   *   If neither or flogger.exclusive or flogger.inclusive is set defaults to true.
   *   If both flogger.exclusive and flogger.inclusive are set defaults to true.
   *   If flogger.exclusive and and logger.&lt;name&gt; is set return false.
   *   If flogger.exclusive and flogger.level return true parameter level is less than.
   *   Otherwise if flogger.exclusive return true.
   *   If flogger.inclusive and and logger.&lt;name&gt; is set return true.
   *   If flogger.inclusive and flogger.level return true parameter level is less false.
   *   Otherwise if flogger.inclusive return false.
   * </pre>
   *
   * @param level the given level.
   * @return true if the given level is loggable, otherwise false.
   */
  @Override
  public boolean isLoggable(Level level) {
    String exclusive = System.getProperty(EXCLUSIVE);
    String inclusive = System.getProperty(INCLUSIVE);
    if (exclusive == null && inclusive == null) return true;
    if (exclusive != null && inclusive != null) return true;

    String name = System.getProperty("flogger." + level.getName());
    Integer value;
    try {
      value = Integer.valueOf(System.getProperty(LEVEL));
    } catch (Exception exception) {
      value = null;
    }
    
    if (exclusive != null) {
      if (name != null) return false;
      if (value != null) return level.intValue() < value;
      return true;
    } else {
      if (name != null) return true;
      if (value != null) return level.intValue() >= value;
      return false;
    }
  }
}
