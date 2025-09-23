package com.onlyoffice.slack.domain.slack.event.registry;

import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import java.util.List;

public interface SlackViewSubmissionActionRegistrar {
  List<String> getIds();

  ViewSubmissionHandler getAction();
}
