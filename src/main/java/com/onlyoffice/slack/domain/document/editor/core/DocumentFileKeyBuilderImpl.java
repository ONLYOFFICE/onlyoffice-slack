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
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
class DocumentFileKeyBuilderImpl implements DocumentFileKeyBuilder {

  @Override
  public String build(
      @NotBlank final String fileId,
      @NotBlank final String teamId,
      @NotBlank final String userId,
      @NotBlank final String uuid) {
    return "%s_%s_%s_%s".formatted(fileId, teamId, userId, UUID.randomUUID().toString());
  }
}
