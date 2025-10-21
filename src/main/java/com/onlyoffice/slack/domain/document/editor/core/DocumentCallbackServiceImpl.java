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
package com.onlyoffice.slack.domain.document.editor.core;

import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.slack.domain.document.event.registry.DocumentCallbackRegistry;
import com.onlyoffice.slack.domain.slack.settings.SettingsService;
import com.onlyoffice.slack.shared.configuration.ServerConfigurationProperties;
import com.onlyoffice.slack.shared.exception.domain.DocumentCallbackException;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
class DocumentCallbackServiceImpl implements DocumentCallbackService {
  private final ServerConfigurationProperties serverConfigurationProperties;

  private final DocumentFileKeyExtractor documentFileKeyExtractor;

  private final DocumentJwtManagerService documentJwtManagerService;
  private final DocumentCallbackRegistry documentCallbackRegistry;
  private final SettingsService settingsService;

  @Override
  public void processCallback(
      @NotNull final Map<String, String> headers, @NotNull final Callback callback) {
    var teamId =
        documentFileKeyExtractor.extract(callback.getKey(), DocumentFileKeyExtractor.Type.TEAM);
    var userId =
        documentFileKeyExtractor.extract(callback.getKey(), DocumentFileKeyExtractor.Type.USER);
    if (teamId == null || teamId.isBlank()) {
      log.warn("Received an invalid callback key. Team id is missing or blank");
      return;
    }

    if (userId == null || userId.isBlank()) {
      log.warn("Received an invalid callback key. User id is missing or blank");
      return;
    }

    try {
      MDC.put("team_id", teamId);
      MDC.put("user_id", userId);
      MDC.put("callback_key", callback.getKey());

      log.info("Processing callback");

      var settings = settingsService.findSettings(teamId);
      if (settings == null)
        throw new DocumentCallbackException(
            "Could not find settings for team %s".formatted(teamId));

      if (settings.isDemoEnabled()) {
        log.info("Demo mode enabled for current team. Using demo server settings");
        settings.setAddress(serverConfigurationProperties.getDemo().getAddress());
        settings.setHeader(serverConfigurationProperties.getDemo().getHeader());
        settings.setSecret(serverConfigurationProperties.getDemo().getSecret());
      }

      if (callback.getToken() == null || callback.getToken().isBlank()) {
        log.info(
            "No token in callback. Attempting to extract from headers using header {}",
            settings.getHeader());
        callback.setToken(headers.get(settings.getHeader()));
      }

      var validatedCallback =
          documentJwtManagerService.verifyToken(callback.getToken(), settings.getSecret());
      if (validatedCallback == null || validatedCallback.isEmpty())
        throw new DocumentCallbackException("Could not validate callback. Malformed value");

      log.info("Callback token validated successfully for current team");

      documentCallbackRegistry
          .find(callback.getStatus())
          .ifPresentOrElse(
              handler -> {
                log.info("Invoking callback handler for status {}", callback.getStatus());
                handler.apply(teamId, userId, callback);
              },
              () -> log.info("No handler found for callback status {}", callback.getStatus()));
    } finally {
      MDC.clear();
    }
  }
}
