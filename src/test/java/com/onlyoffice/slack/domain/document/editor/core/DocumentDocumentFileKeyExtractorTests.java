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

class DocumentDocumentFileKeyExtractorTests {
  @Test
  void whenValidKeyAndType_thenReturnsCorrectPart() {
    DocumentFileKeyExtractor extractor = (key, type) -> key.split("_")[type.ordinal()];
    var key = "file_team_user_id";

    assertEquals("file", extractor.extract(key, DocumentFileKeyExtractor.Type.FILE));
    assertEquals("team", extractor.extract(key, DocumentFileKeyExtractor.Type.TEAM));
    assertEquals("user", extractor.extract(key, DocumentFileKeyExtractor.Type.USER));
  }

  @Test
  void whenMalformedKey_thenThrowsException() {
    DocumentFileKeyExtractor extractor = (key, type) -> key.split("_")[type.ordinal()];
    var key = "file_team";

    assertThrows(
        ArrayIndexOutOfBoundsException.class,
        () -> extractor.extract(key, DocumentFileKeyExtractor.Type.USER));
  }
}
