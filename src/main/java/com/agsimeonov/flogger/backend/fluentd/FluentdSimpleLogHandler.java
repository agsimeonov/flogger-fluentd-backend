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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;

import com.google.common.flogger.backend.SimpleMessageFormatter;

/** A simple log handler used to populate Fluentd output maps. */ 
final class FluentdSimpleLogHandler implements SimpleMessageFormatter.SimpleLogHandler {

  private final Map<String, Object> out;

  /**
   * Creates a simple log handler responsible for populating a Fluentd output map with a
   * formatted log message.
   *
   * @param out the Fluentd output map to populate with a formatted log message.
   */
  FluentdSimpleLogHandler(Map<String, Object> out) {
    this.out = out;
  }

  @Override
  public void handleFormattedLogMessage(Level level, String message, Throwable thrown) {
    if (level != null) out.put("level", level.getName());
    if (message != null) {
      if (!message.startsWith("[CONTEXT")) {
        if (message.endsWith("]")) message = message.split(" \\[CONTEXT")[0];
        if (!message.isEmpty()) out.put("message", message);
      }
    }
    if (thrown != null) {
      out.put("thrown", thrown);
      try (StringWriter stringWriter = new StringWriter()) {
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
          thrown.printStackTrace(printWriter);
          out.put("stackTrace", stringWriter.toString());
        }
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }
  }
}
