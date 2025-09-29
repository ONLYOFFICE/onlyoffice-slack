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
