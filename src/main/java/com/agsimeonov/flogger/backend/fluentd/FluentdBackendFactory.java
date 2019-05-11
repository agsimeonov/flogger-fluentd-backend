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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.Platform.LogCallerFinder;
import com.google.common.flogger.backend.system.BackendFactory;
import com.google.common.flogger.backend.system.DefaultPlatform;
import com.google.common.flogger.backend.system.StackBasedCallerFinder;

import org.fluentd.logger.FluentLogger;

/**
 * BackendFactory for Fluentd.
 *
 * <p>To configure this backend for Flogger set the following system property (also see {@link
 * com.google.common.flogger.backend.system.DefaultPlatform}):
 *
 * <ul>
 *   <li>{@code flogger.backend_factory=com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory#getInstance}).
 * </ul>
 */
public final class FluentdBackendFactory extends BackendFactory {

  private static final String CALLER_FINDER = "caller_finder";
  private static final String REMOTE_SETTINGS = "remote_settings";
  private static final String LEVEL_DISABLER = "level_disabler";

  private static final FluentdBackendFactory INSTANCE = new FluentdBackendFactory();

  private final Map<String, LoggerBackend> loggerBackends = new HashMap<>();

  static {
    try {
      LogCallerFinder callerFinder = resolveAttribute(CALLER_FINDER, LogCallerFinder.class);
      if (callerFinder != null) {
        Field field = StackBasedCallerFinder.class.getDeclaredField("INSTANCE");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.setAccessible(true);
        field.set(null, callerFinder);
      }
    } catch (ReflectiveOperationException exception) {
      exception.printStackTrace();
    }
  }

  /** BackendFactory for Fluentd. */
  private FluentdBackendFactory() {}

  /**
   * This method is expected to be called via reflection (and might otherwise be unused).
   *
   * @return the BackendFactory singleton.
   */
  public static BackendFactory getInstance() {
    return INSTANCE;
  }

  @Override
  public synchronized LoggerBackend create(String loggingClassName) {
    if (loggerBackends.containsKey(loggingClassName)) return loggerBackends.get(loggingClassName);
    FluentdRemoteSettings remoteSettings = resolveAttribute(REMOTE_SETTINGS, FluentdRemoteSettings.class);
    FluentLogger logger = remoteSettings != null
                        ? FluentLogger.getLogger(loggingClassName.replace('$', '.'), remoteSettings.getHost(), remoteSettings.getPort())
                        : FluentLogger.getLogger(loggingClassName.replace('$', '.'));
    LoggerBackend result = new FluentdLoggerBackend(logger, resolveAttribute(LEVEL_DISABLER, FluentdLevelDisabler.class));
    loggerBackends.put(loggingClassName, result);
    return result;
  }

  @Override
  public String toString() {
    return "Fluentd backend";
  }

  /**
   * Helper to call a static no-arg getter to obtain an instance of a specified type. This is used
   * for platform aspects which are optional, but are expected to have a singleton available.
   *
   * @param <T> the type of the instance.
   * @param attributeName the system property specifying an instance of a class.
   * @param type the type of the instance.
   *
   * @return the return value of the specified static no-argument method, or null if the method
   *     cannot be called or the returned value is of the wrong type.
   *
   * @see com.google.common.flogger.backend.system.DefaultPlatform#resolveAttribute
   */
  @Nullable
  @SuppressWarnings("unchecked")
  private static <T> T resolveAttribute(String attributeName, Class<T> type) {
    try {
      Method resolveAttribute = DefaultPlatform.class.getDeclaredMethod("resolveAttribute", String.class, Class.class);
      resolveAttribute.setAccessible(true);
      return (T) resolveAttribute.invoke(null, attributeName, type);
    } catch (ReflectiveOperationException exception) {
      exception.printStackTrace();
      return null;
    }
  }
}
