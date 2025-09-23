package com.onlyoffice.slack.domain.slack.event.registry;

import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
class SlackViewSubmissionRegistryImpl implements SlackViewSubmissionRegistry {
  private final Map<String, ViewSubmissionHandler> registry = new HashMap<>();

  @Autowired
  public SlackViewSubmissionRegistryImpl(
      final List<SlackViewSubmissionActionRegistrar> registrars) {
    for (var registrar : registrars) {
      for (var action : registrar.getIds()) register(action, registrar.getAction());
    }
  }

  @Override
  public void register(final String callbackId, final ViewSubmissionHandler viewSubmissionHandler) {
    registry.putIfAbsent(callbackId, viewSubmissionHandler);
  }

  @Override
  public Optional<ViewSubmissionHandler> find(final String callbackId) {
    return Optional.ofNullable(registry.get(callbackId));
  }
}
