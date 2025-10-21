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
import com.onlyoffice.slack.shared.transfer.cache.DocumentSessionKey;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentFileSessionScheduler {
  private final DocumentFileSessionRepository repository;
  private final IMap<String, DocumentSessionKey> keys;

  @Scheduled(cron = "0 0/15 * * * *")
  public void removeStaleSessions() {
    var ids =
        repository.findLastStaleRecords(LocalDateTime.now().minusDays(7)).stream()
            .collect(Collectors.toMap(s -> s, s -> s));
    keys.removeAll((map) -> ids.containsKey(map.getKey()));
  }
}
