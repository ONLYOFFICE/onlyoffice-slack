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
package com.onlyoffice.slack.domain.document;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyoffice.manager.document.DefaultDocumentManager;
import com.onlyoffice.model.common.Format;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentServerFormatsConfiguration {
  private static final String DOCS_FORMATS_JSON_PATH =
      "document-formats/onlyoffice-docs-formats.json";

  private static final List<Format> formats;

  static {
    var objectMapper = new ObjectMapper();

    var inputStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(DOCS_FORMATS_JSON_PATH);

    if (inputStream == null)
      inputStream =
          DefaultDocumentManager.class.getClassLoader().getResourceAsStream(DOCS_FORMATS_JSON_PATH);

    try {
      formats =
          objectMapper
              .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
              .readValue(inputStream, new TypeReference<List<Format>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Format> getFormats() {
    return formats;
  }
}
