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
package com.onlyoffice.slack.domain.slack.settings;

import static org.junit.jupiter.api.Assertions.*;

import com.onlyoffice.slack.shared.transfer.request.SubmitSettingsRequest;
import com.onlyoffice.slack.shared.transfer.response.SettingsResponse;
import com.slack.api.bolt.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettingsServiceTests {
  private SettingsService service;

  @BeforeEach
  void setUp() {
    service =
        new SettingsService() {
          boolean saved = false;

          @Override
          public void saveSettings(Context ctx, SubmitSettingsRequest request) {
            saved = true;
          }

          @Override
          public SettingsResponse findSettings(String teamId) {
            return SettingsResponse.builder().address("addr").build();
          }

          @Override
          public SettingsResponse alwaysFindSettings(String teamId) {
            return SettingsResponse.builder().address("always").build();
          }
        };
  }

  @Test
  void whenSaveSettings_thenNoException() {
    assertDoesNotThrow(() -> service.saveSettings(null, null));
  }

  @Test
  void whenFindSettings_thenReturnSettingsResponse() {
    var resp = service.findSettings("team");

    assertNotNull(resp);
    assertEquals("addr", resp.getAddress());
  }

  @Test
  void whenAlwaysFindSettings_thenReturnSettingsResponse() {
    var resp = service.alwaysFindSettings("team");

    assertNotNull(resp);
    assertEquals("always", resp.getAddress());
  }
}
