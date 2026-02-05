/**
 * (c) Copyright Ascensio System SIA 2026
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DocumentDocumentFileKeyBuilderTests {
  @Test
  void whenValidInput_thenReturnsConcatenatedKey() {
    DocumentFileKeyBuilder builder =
        (fileId, teamId, userId, uuid) -> fileId + "_" + teamId + "_" + userId + "_" + uuid;

    var result = builder.build("file", "team", "user", "id");

    assertEquals("file_team_user_id", result);
  }

  @Test
  void whenAnyInputIsBlank_thenThrowsException() {
    DocumentFileKeyBuilder builder =
        (fileId, teamId, userId, uuid) -> {
          if (fileId.isBlank() || teamId.isBlank() || userId.isBlank() || uuid.isBlank())
            throw new IllegalArgumentException("Arguments must not be blank");
          return fileId + teamId + userId + uuid;
        };
    assertThrows(IllegalArgumentException.class, () -> builder.build("", "team", "user", "id"));
    assertThrows(IllegalArgumentException.class, () -> builder.build("file", "", "user", "id"));
    assertThrows(IllegalArgumentException.class, () -> builder.build("file", "team", "", "id"));
    assertThrows(IllegalArgumentException.class, () -> builder.build("file", "team", "user", ""));
  }
}
