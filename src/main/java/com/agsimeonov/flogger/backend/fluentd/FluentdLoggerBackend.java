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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.common.flogger.LogSite;
import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.backend.SimpleMessageFormatter;
import com.google.common.flogger.backend.system.SimpleLogRecord;

import org.fluentd.logger.FluentLogger;

/** A logging backend that uses Fluentd to output log statements. */
final class FluentdLoggerBackend extends LoggerBackend {

  private final FluentLogger logger;

  /**
   * A logging backend that uses Fluentd to output log statements..
   *
   * @param logger the Fluentd logger.
   */
  FluentdLoggerBackend(FluentLogger logger) {
    this.logger = logger;
  }

  @Override
  public String getLoggerName() {
    return logger.getName();
  }

  @Override
  public boolean isLoggable(Level level) {
    return true;
  }

  @Override
  public void log(LogData data) {
    Map<String, Object> out = new HashMap<String, Object>();
    SimpleMessageFormatter.format(data, new FluentdSimpleLogHandler(out));
    out.put("timestampNanos", data.getTimestampNanos());
    if (data.getLogSite() != null) {
      LogSite logSite = data.getLogSite();
      if (logSite.getClassName() != null) out.put("className", logSite.getClassName());
      if (logSite.getFileName() != null) out.put("fileName", logSite.getFileName());
      out.put("lineNumber", logSite.getLineNumber());
      if (logSite.getMethodName() != null) out.put("methodName", logSite.getMethodName());
    }
    if (data.getMetadata() != null) {
      Metadata metadata = data.getMetadata();
      for (int i = 0; i < metadata.size(); i++) {
        out.put(metadata.getKey(i).getLabel(), metadata.getValue(i));
      }
    }
    logger.log(data.getLevel().getClass().getSimpleName(), out, TimeUnit.SECONDS.convert(data.getTimestampNanos(), TimeUnit.NANOSECONDS));
  }

  @Override
  public void handleError(RuntimeException error, LogData badData) {
    Map<String, Object> out = new HashMap<String, Object>();
    out.put("level", Level.SEVERE.getName());
    out.put("message", SimpleLogRecord.error(error, badData));
    logger.log(Level.SEVERE.getName(), out);
  }
}
