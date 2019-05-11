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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.LogSite;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FluentdCallerFinderTest extends FluentdCallerFinder {

  @ParameterizedTest
  @CsvSource("com.agsimeonov.flogger.backend.fluentd.FluentdCallerFinder, -1")
  public void testFindLogSite(@SuppressWarnings("rawtypes") Class loggerApi, int stackFramesToSkip) {
    LogSite logSite = super.findLogSite(loggerApi, stackFramesToSkip);
    assertNotNull(logSite);
  }

  @Override
  public String findLoggingClass(Class<? extends AbstractLogger<?>> loggerClass) {
    return null;
  }
}
