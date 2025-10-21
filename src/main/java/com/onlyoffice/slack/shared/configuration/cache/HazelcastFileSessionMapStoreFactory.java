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

import com.hazelcast.map.MapStore;
import com.hazelcast.map.MapStoreFactory;
import com.onlyoffice.slack.domain.document.session.DocumentFileSessionService;
import com.onlyoffice.slack.shared.transfer.cache.DocumentSessionKey;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class HazelcastFileSessionMapStoreFactory
    implements MapStoreFactory<String, DocumentSessionKey>, ApplicationContextAware {
  private static ApplicationContext staticApplicationContext;

  @Override
  public void setApplicationContext(final @NotNull ApplicationContext context) {
    HazelcastFileSessionMapStoreFactory.staticApplicationContext = context;
  }

  @Override
  public MapStore<String, DocumentSessionKey> newMapStore(
      final String mapName, final Properties properties) {
    if (staticApplicationContext == null)
      throw new IllegalStateException(
          "ApplicationContext not initialized in HazelcastFileSessionMapStoreFactory");
    return staticApplicationContext.getBean(DocumentFileSessionService.class);
  }
}
