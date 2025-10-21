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
