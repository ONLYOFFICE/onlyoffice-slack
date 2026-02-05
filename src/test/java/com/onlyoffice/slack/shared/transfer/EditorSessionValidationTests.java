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
package com.onlyoffice.slack.shared.transfer;

import static org.junit.jupiter.api.Assertions.*;

import com.onlyoffice.slack.shared.transfer.cache.EditorSession;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EditorSessionValidationTests {
  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void whenAnyFieldIsBlank_thenValidationFails() {
    var session =
        EditorSession.builder()
            .teamId("")
            .userId("")
            .userName("")
            .fileId("")
            .fileName("")
            .channelId("")
            .messageTs("")
            .build();
    var violations = validator.validate(session);
    assertFalse(violations.isEmpty());
  }

  @Test
  void whenAllFieldsValid_thenValidationPasses() {
    var session =
        EditorSession.builder()
            .teamId("t")
            .userId("u")
            .userName("n")
            .fileId("f")
            .fileName("fn")
            .channelId("c")
            .messageTs("m")
            .build();
    var violations = validator.validate(session);
    assertTrue(violations.isEmpty());
  }
}
