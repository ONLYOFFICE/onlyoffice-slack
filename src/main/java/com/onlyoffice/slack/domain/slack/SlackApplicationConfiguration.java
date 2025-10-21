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
package com.onlyoffice.slack.domain.slack;

import com.onlyoffice.slack.domain.slack.event.registry.SlackBlockActionRegistry;
import com.onlyoffice.slack.domain.slack.event.registry.SlackSlashCommandHandlerRegistry;
import com.onlyoffice.slack.domain.slack.event.registry.SlackViewSubmissionRegistry;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.handler.builtin.MessageShortcutHandler;
import com.slack.api.bolt.service.InstallationService;
import com.slack.api.bolt.service.OAuthStateService;
import com.slack.api.bolt.service.builtin.oauth.view.OAuthInstallPageRenderer;
import com.slack.api.model.event.AppHomeOpenedEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Setter
@Getter
@Configuration
@RequiredArgsConstructor
public class SlackApplicationConfiguration {
  private final SlackConfigurationProperties properties;

  private final SlackBlockActionRegistry blockActionRegistry;
  private final SlackViewSubmissionRegistry viewSubmissionRegistry;
  private final SlackSlashCommandHandlerRegistry commandHandlerRegistry;

  private final BoltEventHandler<AppHomeOpenedEvent> appHomeOpenedEventBoltEventHandler;
  private final MessageShortcutHandler slackMessageShortcutHandler;

  @Bean
  public AppConfig slackBoltApplicationConfiguration(
      final OAuthInstallPageRenderer installPageRenderer) {
    return AppConfig.builder()
        .clientId(properties.getClientId())
        .clientSecret(properties.getClientSecret())
        .signingSecret(properties.getSigningSecret())
        .scope(properties.getScopes())
        .userScope(properties.getUserScopes())
        .oauthInstallPath(properties.getPathInstallation())
        .oAuthInstallPageRenderer(installPageRenderer)
        .oauthRedirectUriPath(properties.getPathRedirect())
        .oauthCompletionUrl(properties.getPathCompletion())
        .oauthCancellationUrl(properties.getPathCancellation())
        .alwaysRequestUserTokenNeeded(true)
        .build();
  }

  @Bean
  public App slackBoltApplication(
      final AppConfig slackBoltApplicationConfiguration,
      final InstallationService onlyofficeInstallationService,
      final OAuthStateService stateService) {
    var app =
        new App(slackBoltApplicationConfiguration)
            .service(onlyofficeInstallationService)
            .service(stateService)
            .asOAuthApp(true)
            .enableTokenRevocationHandlers();

    app.event(AppHomeOpenedEvent.class, appHomeOpenedEventBoltEventHandler);
    app.messageShortcut(properties.getFileManagerShortcutId(), slackMessageShortcutHandler);
    blockActionRegistry.getRegistry().forEach(app::blockAction);
    commandHandlerRegistry.getRegistry().forEach(app::command);
    viewSubmissionRegistry.getRegistry().forEach(app::viewSubmission);

    return app;
  }
}
