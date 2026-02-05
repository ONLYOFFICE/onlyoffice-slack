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
package com.onlyoffice.slack.domain.document.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hazelcast.map.IMap;
import com.onlyoffice.slack.shared.transfer.cache.EditorSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DocumentFileSessionStatusControllerTests {
  private DocumentFileSessionStatusController controller;
  private IMap<String, EditorSession> sessions;

  @BeforeEach
  void setUp() {
    sessions = mock(IMap.class);
    controller = new DocumentFileSessionStatusController(sessions);
  }

  @Test
  void whenSessionExists_thenReturnsReadyStatus() {
    var sessionId = "session-id";
    var session = mock(EditorSession.class);

    when(sessions.get(sessionId)).thenReturn(session);

    var result = controller.checkSessionStatus(sessionId);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    Assertions.assertNotNull(result.getBody());
    assertTrue(result.getBody().isReady());
  }

  @Test
  void whenSessionDoesNotExist_thenReturnsNotReadyStatus() {
    var sessionId = "bad-session-id";

    when(sessions.get(sessionId)).thenReturn(null);

    var result = controller.checkSessionStatus(sessionId);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    Assertions.assertNotNull(result.getBody());
    assertFalse(result.getBody().isReady());
  }
}
