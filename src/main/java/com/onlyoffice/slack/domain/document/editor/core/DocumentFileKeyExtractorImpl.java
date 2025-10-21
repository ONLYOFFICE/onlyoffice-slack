/**
 * (c) Copyright Ascensio System SIA 2025
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onlyoffice.slack.domain.document.editor.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
class DocumentFileKeyExtractorImpl implements DocumentFileKeyExtractor {

  @Override
  public String extract(@NotBlank final String key, @NotNull final Type type) {
    try {
      var parts = key.split("_");
      if (parts.length != 4) {
        MDC.put("length", String.valueOf(parts.length));
        log.error("Malformed document key. Invalid number of parts");
        return null;
      }

      switch (type) {
        case FILE -> {
          return parts[0];
        }
        case TEAM -> {
          return parts[1];
        }
        case USER -> {
          return parts[2];
        }
        default -> {
          return parts[3];
        }
      }
    } finally {
      MDC.clear();
    }
  }
}
