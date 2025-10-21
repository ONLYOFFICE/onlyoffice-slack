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
package com.onlyoffice.slack.domain.document.event.handler;

import com.hazelcast.map.IMap;
import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.model.documenteditor.callback.Status;
import com.onlyoffice.slack.domain.document.editor.core.DocumentFileKeyExtractor;
import com.onlyoffice.slack.domain.document.event.registry.DocumentCallbackRegistrar;
import com.onlyoffice.slack.shared.transfer.cache.DocumentSessionKey;
import com.onlyoffice.slack.shared.utils.TriFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class DocumentClosedCallbackHandler implements DocumentCallbackRegistrar {
  private final DocumentFileKeyExtractor documentFileKeyExtractor;
  private final IMap<String, DocumentSessionKey> keys;

  @Override
  public Status getStatus() {
    return Status.CLOSED;
  }

  @Override
  public TriFunction<String, String, Callback, Callback> getHandler() {
    return (teamId, userId, callback) -> {
      try {
        var fileId =
            documentFileKeyExtractor.extract(callback.getKey(), DocumentFileKeyExtractor.Type.FILE);

        if (fileId == null) return callback;

        MDC.put("file_id", fileId);

        if (callback.getUsers() == null || callback.getUsers().isEmpty()) {
          log.info("Removing an active session on all users exit for current file");
          keys.remove(fileId);
        }

        return callback;
      } finally {
        MDC.clear();
      }
    };
  }
}
