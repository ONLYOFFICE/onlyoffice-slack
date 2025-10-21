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

import com.onlyoffice.model.documenteditor.config.Document;
import com.onlyoffice.model.documenteditor.config.document.DocumentType;
import com.slack.api.model.File;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface DocumentFileManagerService {
  String getExtension(@NotNull final File file);

  DocumentType getDocumentType(@NotNull final File file);

  boolean isEditable(@NotNull final File file);

  Document getDocument(
      @NotBlank final String teamId,
      @NotBlank final String userId,
      @NotNull final File file,
      @NotBlank final String channelId,
      @NotBlank final String messageTs);
}
