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

import java.lang.Package;
import java.io.FileReader;
import java.io.IOException;

import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.backend.Platform.LogCallerFinder;

import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Caller finder utilizing the implementation title as the tax prefix.
 *
 * <p>To configure set the following system properties (also see {@link com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory}:
 *
 * <ul>
 *   <li>{@code flogger.caller_finder=com.trove.platform.logging.ImplementationTitleCallerFinder#getInstance}.
 * </ul>
 */
public class ImplementationTitleCallerFinder extends FluentdCallerFinder {

  private static final LogCallerFinder INSTANCE = new ImplementationTitleCallerFinder();

  /** Caller finder utilizing the implementation title as the tag prefix. */
  private ImplementationTitleCallerFinder() {}

  /**
   * Acquires a singleton ImplementationTitleCallerFinder.
   *
   * @return the ImplementationTitleCallerFinder singleton.
   */
  public static LogCallerFinder getInstance() {
    return INSTANCE;
  }

  @Override
  public String findLoggingClass(Class<? extends AbstractLogger<?>> loggerClass) {
    String implementationTitle;

    Package pack = this.getClass().getPackage();
    if (pack != null) {
      implementationTitle = pack.getImplementationTitle();
      if (implementationTitle != null) return implementationTitle;
    }

    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      implementationTitle = reader.read(new FileReader("pom.xml")).getArtifactId();
      if (implementationTitle != null) return implementationTitle;
    } catch (IOException | XmlPullParserException exception) {}
    throw new IllegalStateException("Implementation title was not found!");
  }
}
