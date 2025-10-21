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
package com.onlyoffice.slack.shared.configuration.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.onlyoffice.slack.shared.persistence.entity.id.InstallerUserId;
import com.onlyoffice.slack.shared.transfer.cache.DocumentSessionKey;
import com.onlyoffice.slack.shared.transfer.cache.EditorSession;
import com.slack.api.bolt.model.builtin.DefaultBot;
import com.slack.api.bolt.model.builtin.DefaultInstaller;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HazelcastConfiguration {
  private static final String BOTS_MAP_KEY = "bots";
  private static final String STATES_MAP_KEY = "states";
  private static final String SESSIONS_MAP_KEY = "sessions";
  private static final String DOCUMENT_KEYS_MAP_KEY = "keys";
  private static final String INSTALLERS_MAP_KEY = "installers";

  private final HazelcastInstance hazelcastInstance;

  @Bean
  public IMap<String, Instant> states(final HazelcastInstance instance) {
    return instance.getMap(STATES_MAP_KEY);
  }

  @Bean
  IMap<String, EditorSession> sessions(final HazelcastInstance instance) {
    return instance.getMap(SESSIONS_MAP_KEY);
  }

  @Bean
  public IMap<String, DocumentSessionKey> documentKeys(final HazelcastInstance instance) {
    return instance.getMap(DOCUMENT_KEYS_MAP_KEY);
  }

  @Bean
  public IMap<String, DefaultBot> bot(final HazelcastInstance instance) {
    return instance.getMap(BOTS_MAP_KEY);
  }

  @Bean
  public IMap<InstallerUserId, DefaultInstaller> installerUser(final HazelcastInstance instance) {
    return instance.getMap(INSTALLERS_MAP_KEY);
  }
}
