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

/** Implementing classes are used to determine wheter given levels are loggable. */
public interface FluentdLevelDisabler {

  /**
   * Determines whether given levels are loggable.
   * 
   * @param level the given level.
   * @return true if the given level is loggable, otherwise false.
   */
  public boolean isLoggable(Level level);
}