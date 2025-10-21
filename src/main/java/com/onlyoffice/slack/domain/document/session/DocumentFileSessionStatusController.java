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
package com.onlyoffice.slack.domain.document.session;

import com.hazelcast.map.IMap;
import com.onlyoffice.slack.shared.transfer.cache.EditorSession;
import com.onlyoffice.slack.shared.transfer.response.SessionStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
class DocumentFileSessionStatusController {
  private final IMap<String, EditorSession> sessions;

  @ResponseBody
  @GetMapping(path = "/editor/status")
  public ResponseEntity<SessionStatusResponse> checkSessionStatus(
      @RequestParam("session") final String sessionId) {
    var storedSession = sessions.get(sessionId);
    var ready = storedSession != null;
    return ResponseEntity.ok(SessionStatusResponse.builder().ready(ready).build());
  }
}
