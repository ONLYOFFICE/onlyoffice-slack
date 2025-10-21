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
package com.onlyoffice.slack.domain.slack.state;

import com.hazelcast.map.IMap;
import com.slack.api.bolt.service.OAuthStateService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HazelcastStateService implements OAuthStateService {
  private final IMap<String, Instant> states;

  @Override
  public void addNewStateToDatastore(String state) {
    try {
      MDC.put("state", state);
      log.info("Adding new OAuth state to Hazelcast");

      states.putIfAbsent(state, Instant.now());

      log.info("OAuth state added to Hazelcast");
    } finally {
      MDC.clear();
    }
  }

  @Override
  public boolean isAvailableInDatabase(String state) {
    try {
      MDC.put("state", state);
      log.info("Checking if OAuth state exists in Hazelcast");

      boolean exists = states.containsKey(state);

      log.info("OAuth state exists {} in Hazelcast", exists);

      return exists;
    } finally {
      MDC.clear();
    }
  }

  @Override
  public void deleteStateFromDatastore(String state) throws Exception {
    try {
      MDC.put("state", state);
      log.info("Deleting OAuth state from Hazelcast");

      states.removeAsync(state);
    } finally {
      MDC.clear();
    }
  }
}
