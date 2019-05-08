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

/**
 * Configures remote fluentd loggers via system proprties.
 *
 * <p>To configure set the following system properties (also see {@link com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory}:
 *
 * <ul>
 *   <li>{@code flogger.remote_settings=com.agsimeonov.flogger.backend.fluentd.SystemPropertiesFluentdRemoteLoggerSettings#getInstance}.
 *   <li>{@code flogger.fluentd_host=<fluentd_host>}.
 *   <li>{@code flogger.fluentd_port=<fluentd_port>}.
 * </ul>
 */
public class SystemPropertiesRemoteSettings implements FluentdRemoteSettings {

  private static final String FLUENTD_HOST = "flogger.fluentd_host";
  private static final String FLUENTD_PORT = "flogger.fluentd_port";

  private static final FluentdRemoteSettings INSTANCE = new SystemPropertiesRemoteSettings();

  /** Configures remote fluentd loggers via system proprties. */
  private SystemPropertiesRemoteSettings() {}

  /**
   * Acquires a singleton FluentdRemoteLoggerSettings.
   *
   * @return the FluentdRemoteLoggerSettings singleton.
   */
  public static FluentdRemoteSettings getInstance() {
    return INSTANCE;
  }

  @Override
  public String getHost() {
    String result = System.getProperty(FLUENTD_HOST);
    return result != null ? result : "localhost";
  }

  @Override
  public int getPort() {
    try {
      return Integer.valueOf(System.getProperty(FLUENTD_PORT));
    } catch (Exception exception) {
      return 24224;
    }
  }
}
