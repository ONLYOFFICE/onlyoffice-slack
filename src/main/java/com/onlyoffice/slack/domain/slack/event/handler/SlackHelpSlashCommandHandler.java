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
package com.onlyoffice.slack.domain.slack.event.handler;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;

import com.onlyoffice.slack.domain.slack.event.registry.SlackSlashCommandHandlerRegistrar;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import com.onlyoffice.slack.shared.utils.LocaleUtils;
import com.slack.api.bolt.handler.builtin.SlashCommandHandler;
import com.slack.api.methods.request.users.UsersInfoRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class SlackHelpSlashCommandHandler implements SlackSlashCommandHandlerRegistrar {
  private final MessageSourceSlackConfiguration messageConfig;
  private final SlackConfigurationProperties slackProperties;

  private final MessageSource messageSource;
  private final LocaleUtils localeUtils;

  @Override
  public String getSlash() {
    return "/help";
  }

  @Override
  public SlashCommandHandler getHandler() {
    return (req, ctx) -> {
      try {
        MDC.put("team_id", ctx.getTeamId());
        MDC.put("channel_id", req.getPayload().getChannelId());
        MDC.put("user_id", req.getPayload().getUserId());

        log.info("Sending ephemeral message via /help command");

        var lang = "en-US";
        var userInfo =
            ctx.client()
                .usersInfo(
                    UsersInfoRequest.builder()
                        .user(ctx.getRequestUserId())
                        .token(ctx.getRequestUserToken())
                        .includeLocale(true)
                        .build());

        if (userInfo.isOk() && userInfo.getUser().getLocale() != null)
          lang = userInfo.getUser().getLocale();

        var locale = localeUtils.toLocale(lang);

        var userId = req.getPayload().getUserId();
        var blocks =
            List.of(
                section(
                    section ->
                        section.text(
                            markdownText(
                                messageSource.getMessage(
                                    messageConfig.getMessageHelpGreeting(),
                                    new Object[] {userId},
                                    locale)))),
                section(
                    section ->
                        section.text(
                            markdownText(
                                messageSource.getMessage(
                                    messageConfig.getMessageHelpInstructions(), null, locale)))),
                divider(),
                section(
                    section ->
                        section
                            .text(
                                markdownText(
                                    messageSource.getMessage(
                                        messageConfig.getMessageHelpLearnMore(), null, locale)))
                            .accessory(
                                button(
                                    button ->
                                        button
                                            .text(
                                                plainText(
                                                    messageSource.getMessage(
                                                            messageConfig
                                                                .getMessageHelpLearnMoreButtonEmoji(),
                                                            null,
                                                            locale)
                                                        + " "
                                                        + messageSource.getMessage(
                                                            messageConfig
                                                                .getMessageHelpLearnMoreButton(),
                                                            null,
                                                            locale)))
                                            .actionId(slackProperties.getLearnMoreActionId())
                                            .url(slackProperties.getWelcomeReadMoreUrl())))),
                divider(),
                section(
                    section ->
                        section
                            .text(
                                markdownText(
                                    messageSource.getMessage(
                                        messageConfig.getMessageHelpFeedback(), null, locale)))
                            .accessory(
                                button(
                                    button ->
                                        button
                                            .text(
                                                plainText(
                                                    messageSource.getMessage(
                                                        messageConfig
                                                            .getMessageHelpFeedbackButton(),
                                                        null,
                                                        locale)))
                                            .actionId(slackProperties.getShareFeedbackActionId())
                                            .url(slackProperties.getWelcomeSuggestFeatureUrl())))));

        return ctx.ack(res -> res.blocks(blocks).responseType("ephemeral"));
      } finally {
        MDC.clear();
      }
    };
  }
}
