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
 *   <li>{@code flogger.backend_factory=
 *       com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory#getInstance}.
 * </ul>
 */
public final class FluentdBackendFactory extends BackendFactory {

  private static final FluentdBackendFactory INSTANCE = new FluentdBackendFactory();

  static {
    try {
      Method resolveAttribute = DefaultPlatform.class.getDeclaredMethod("resolveAttribute", String.class, Class.class);
      resolveAttribute.setAccessible(true);
      LogCallerFinder callerFinder = (LogCallerFinder) resolveAttribute.invoke(null, "caller_finder", LogCallerFinder.class);
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

  /** This method is expected to be called via reflection (and might otherwise be unused). */
  public static BackendFactory getInstance() {
    return INSTANCE;
  }

  @Override
  public LoggerBackend create(String loggingClassName) {
    return new FluentdLoggerBackend(FluentLogger.getLogger(loggingClassName.replace('$', '.')));
  }

  @Override
  public String toString() {
    return "Fluentd backend";
  }
}