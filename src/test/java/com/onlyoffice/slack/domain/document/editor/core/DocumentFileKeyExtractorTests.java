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

class DocumentFileKeyExtractorTests {
  private final DocumentFileKeyExtractorImpl extractor = new DocumentFileKeyExtractorImpl();

  @Test
  void whenTypeIsFile_thenReturnsFileId() {
    var key = "file_team_user_id";

    assertEquals("file", extractor.extract(key, DocumentFileKeyExtractor.Type.FILE));
  }

  @Test
  void whenTypeIsTeam_thenReturnsTeamId() {
    var key = "file_team_user_id";

    assertEquals("team", extractor.extract(key, DocumentFileKeyExtractor.Type.TEAM));
  }

  @Test
  void whenTypeIsUser_thenReturnsUserId() {
    var key = "file_team_user_id";

    assertEquals("user", extractor.extract(key, DocumentFileKeyExtractor.Type.USER));
  }

  @Test
  void whenTypeIsNull_thenThrowsNullPointerException() {
    var key = "file_team_user_id";

    assertThrows(NullPointerException.class, () -> extractor.extract(key, null));
  }

  @Test
  void whenMalformedKey_thenReturnsNull() {
    var key = "file_team_user";

    assertNull(extractor.extract(key, DocumentFileKeyExtractor.Type.FILE));
  }
}
