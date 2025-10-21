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
package com.onlyoffice.slack.domain.slack.event.action;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.button;
import static com.slack.api.model.view.Views.*;

import com.onlyoffice.slack.shared.configuration.ServerConfigurationProperties;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import com.onlyoffice.slack.shared.utils.LocaleUtils;
import com.slack.api.bolt.context.builtin.MessageShortcutContext;
import com.slack.api.bolt.handler.builtin.MessageShortcutHandler;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.request.views.ViewsOpenRequest;
import com.slack.api.model.File;
import com.slack.api.model.Message;
import com.slack.api.model.block.LayoutBlock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class SlackOpenFileManagerMessageShortcutRequestHandler implements MessageShortcutHandler {
  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

  private final MessageSourceSlackConfiguration messageSourceSlackConfiguration;
  private final ServerConfigurationProperties serverConfigurationProperties;
  private final SlackConfigurationProperties slackConfigurationProperties;

  private final SlackPrivateMetadataBuilder slackPrivateMetadataBuilder;
  private final SlackFileActionBuilder slackFileActionBuilder;
  private final MessageSource messageSource;
  private final LocaleUtils localeUtils;

  private String buildEditorUrl(final String sessionId, final Locale locale) {
    return slackConfigurationProperties
        .getEditorPathPattern()
        .formatted(serverConfigurationProperties.getBaseAddress(), sessionId, locale);
  }

  private boolean hasFiles(final Message message) {
    return message != null && message.getFiles() != null && !message.getFiles().isEmpty();
  }

  private void addFileBlock(
      final String userId, final List<LayoutBlock> blocks, final File file, final Locale locale) {
    blocks.add(divider());
    if (file.getSize() > MAX_FILE_SIZE) {
      blocks.add(
          section(
              s ->
                  s.text(
                      plainText(
                          file.getName()
                              + "  —  "
                              + (file.getSize() / 1024)
                              + " KB (too large)"))));
      return;
    }

    var sessionId =
        slackFileActionBuilder.build(userId, UUID.randomUUID().toString(), file.getId());
    var openButton =
        button(
            b ->
                b.text(
                        plainText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageManagerModalOpenButton(),
                                null,
                                locale)))
                    .value(sessionId)
                    .url(buildEditorUrl(sessionId, locale))
                    .actionId(slackConfigurationProperties.getOpenFileActionId())
                    .style("primary"));
    blocks.add(
        section(
            s ->
                s.text(plainText(file.getName() + "  —  " + (file.getSize() / 1024) + " KB"))
                    .accessory(openButton)));
  }

  private List<LayoutBlock> buildFileBlocks(
      final String userId, final Message message, final Locale locale) {
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(
        header(
            h ->
                h.text(
                    plainText(
                        messageSource.getMessage(
                            messageSourceSlackConfiguration.getMessageManagerModalHeader(),
                            null,
                            locale)))));

    if (hasFiles(message)) {
      log.info("Found {} files in message", message.getFiles().size());
      message.getFiles().forEach(file -> addFileBlock(userId, blocks, file, locale));
    } else {
      log.info("No files found in message");
      blocks.add(
          section(
              s ->
                  s.text(
                      plainText(
                          messageSource.getMessage(
                              messageSourceSlackConfiguration.getMessageManagerModalNoFilesFound(),
                              null,
                              locale)))));
    }

    return blocks;
  }

  private void openFilesModal(
      final MessageShortcutContext ctx,
      final MessageShortcutRequest request,
      final List<LayoutBlock> blocks,
      final Locale locale)
      throws IOException, SlackApiException {
    var message = request.getPayload().getMessage();
    ctx.client()
        .viewsOpen(
            ViewsOpenRequest.builder()
                .triggerId(request.getPayload().getTriggerId())
                .view(
                    view(
                        view ->
                            view.type("modal")
                                .notifyOnClose(false)
                                .title(
                                    viewTitle(
                                        title ->
                                            title
                                                .type("plain_text")
                                                .text(
                                                    messageSource.getMessage(
                                                        messageSourceSlackConfiguration
                                                            .getMessageManagerModalTitle(),
                                                        null,
                                                        locale))))
                                .close(
                                    viewClose(
                                        close ->
                                            close
                                                .type("plain_text")
                                                .text(
                                                    messageSource.getMessage(
                                                        messageSourceSlackConfiguration
                                                            .getMessageManagerModalCloseButton(),
                                                        null,
                                                        locale))))
                                .blocks(blocks)
                                .privateMetadata(
                                    slackPrivateMetadataBuilder.build(
                                        ctx.getChannelId(), message.getTs()))))
                .build());
  }

  @Override
  public Response apply(MessageShortcutRequest request, MessageShortcutContext ctx)
      throws IOException, SlackApiException {
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

    openFilesModal(
        ctx,
        request,
        buildFileBlocks(ctx.getRequestUserId(), request.getPayload().getMessage(), locale),
        locale);
    return ctx.ack();
  }
}
