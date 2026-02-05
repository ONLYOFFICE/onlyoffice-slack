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

import com.onlyoffice.slack.shared.transfer.command.DocumentServerCommand;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DocumentServerCommandValidationTests {
  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void whenCommandIsBlank_thenValidationFails() {
    var cmd = DocumentServerCommand.builder().c("").build();
    var violations = validator.validate(cmd);
    assertFalse(violations.isEmpty());
  }

  @Test
  void whenCommandIsNull_thenValidationFails() {
    var cmd = DocumentServerCommand.builder().c(null).build();
    var violations = validator.validate(cmd);
    assertFalse(violations.isEmpty());
  }

  @Test
  void whenCommandIsNotBlank_thenValidationPasses() {
    var cmd = DocumentServerCommand.builder().c("valid").build();
    var violations = validator.validate(cmd);
    assertTrue(violations.isEmpty());
  }
}
