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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.flogger.backend.KeyValueHandler;

/** Key Value handler employed when logging out Tags to Fluentd. */
public class FluentdTagsKeyValueHandler implements KeyValueHandler {

  private final String label;
  private final Map<String, Object> out;

  /**
   * Key Value handler employed when logging out Tags to Fluentd.
   * 
   * @param label the label provided in the Flogger metadata.
   * @param out the output map sent to Fluentd.
   */
  public FluentdTagsKeyValueHandler(String label, Map<String, Object> out) {
    this.label = label;
    this.out = out;
  }

  @Override
  public KeyValueHandler handle(String key, Object value) {
    if (value == null) {
      if (out.containsKey(label)) {
        if (!(out.get(label) instanceof List)) out.put(label, new ArrayList<>());
      } else {
        out.put(label, new ArrayList<>());
      }
      @SuppressWarnings("unchecked")
      ArrayList<Object> tags = (ArrayList<Object>) out.get(label);
      tags.add(key);
    } else {
      out.put(key, value);
    }
    return this;
  }
}
