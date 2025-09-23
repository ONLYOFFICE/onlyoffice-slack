package com.onlyoffice.slack.domain.slack.event.registry;

import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import java.util.Map;
import java.util.Optional;

public interface SlackBlockActionRegistry {
  void register(String callbackId, BlockActionHandler actionHandler);

  Optional<BlockActionHandler> find(String callbackId);

  Map<String, BlockActionHandler> getRegistry();
}
