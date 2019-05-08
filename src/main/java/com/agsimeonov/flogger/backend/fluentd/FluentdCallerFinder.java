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

import com.google.common.flogger.LogSite;
import com.google.common.flogger.backend.Platform.LogCallerFinder;
import com.google.common.flogger.util.CallerFinder;
import com.google.common.flogger.util.StackBasedLogSite;

/** The base for Fluentd caller finders which makes sure to set the log site based on the stack. */
public abstract class FluentdCallerFinder extends LogCallerFinder {

  @Override
  public LogSite findLogSite(Class<?> loggerApi, int stackFramesToSkip) {
    StackTraceElement caller = CallerFinder.findCallerOf(loggerApi, new Throwable(), stackFramesToSkip + 1);
    return caller != null ? new StackBasedLogSite(caller) : LogSite.INVALID;
  }
}
