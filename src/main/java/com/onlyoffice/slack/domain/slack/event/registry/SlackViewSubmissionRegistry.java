package com.onlyoffice.slack.domain.slack.event.registry;

import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import java.util.Map;
import java.util.Optional;

public interface SlackViewSubmissionRegistry {
  void register(String callbackId, ViewSubmissionHandler viewSubmissionHandler);

  Optional<ViewSubmissionHandler> find(String callbackId);

  Map<String, ViewSubmissionHandler> getRegistry();
}
