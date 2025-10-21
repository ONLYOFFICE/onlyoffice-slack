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
package com.onlyoffice.slack.shared.utils;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class LocaleUtils {
  public Locale toLocale(String localeString) {
    if (localeString == null || localeString.isBlank()) return Locale.ENGLISH;

    try {
      if (localeString.contains("-")) {
        var parts = localeString.split("-", 2);
        if (parts.length == 2) return Locale.of(parts[0], parts[1]);
      }

      return Locale.of(localeString);
    } catch (Exception e) {
      return Locale.ENGLISH;
    }
  }
}
