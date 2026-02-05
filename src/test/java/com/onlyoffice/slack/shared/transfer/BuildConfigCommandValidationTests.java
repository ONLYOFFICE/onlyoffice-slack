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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.onlyoffice.model.documenteditor.config.document.Type;
import com.onlyoffice.model.documenteditor.config.editorconfig.Mode;
import com.onlyoffice.slack.shared.transfer.command.BuildConfigCommand;
import com.slack.api.model.File;
import com.slack.api.model.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BuildConfigCommandValidationTests {
  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void whenRequiredFieldsAreNullOrBlank_thenValidationFails() {
    var cmd =
        BuildConfigCommand.builder()
            .channelId("")
            .messageTs(null)
            .signingSecret("")
            .user(null)
            .file(null)
            .mode(null)
            .type(null)
            .build();
    var violations = validator.validate(cmd);
    assertFalse(violations.isEmpty());
  }

  @Test
  void whenAllFieldsValid_thenValidationPasses() {
    var cmd =
        BuildConfigCommand.builder()
            .channelId("channel")
            .messageTs("ts")
            .signingSecret("secret")
            .user(mock(User.class))
            .file(mock(File.class))
            .mode(mock(Mode.class))
            .type(mock(Type.class))
            .locale("en")
            .build();
    var violations = validator.validate(cmd);
    assertTrue(violations.isEmpty());
  }
}
