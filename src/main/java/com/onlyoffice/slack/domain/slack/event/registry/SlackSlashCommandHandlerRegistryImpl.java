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

import com.slack.api.bolt.handler.builtin.SlashCommandHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
class SlackSlashCommandHandlerRegistryImpl implements SlackSlashCommandHandlerRegistry {
  private final Map<String, SlashCommandHandler> registry = new HashMap<>();

  @Autowired
  public SlackSlashCommandHandlerRegistryImpl(
      final List<SlackSlashCommandHandlerRegistrar> registrars) {
    for (var registrar : registrars) {
      register(registrar.getSlash(), registrar.getHandler());
    }
  }

  @Override
  public void register(final String slash, final SlashCommandHandler handler) {
    registry.putIfAbsent(slash, handler);
  }

  @Override
  public Optional<SlashCommandHandler> find(final String slash) {
    return Optional.ofNullable(registry.get(slash));
  }
}
