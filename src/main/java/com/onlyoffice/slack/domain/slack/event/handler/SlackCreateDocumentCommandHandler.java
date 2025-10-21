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

import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.composition.BlockCompositions.option;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.plainTextInput;
import static com.slack.api.model.block.element.BlockElements.staticSelect;
import static com.slack.api.model.view.Views.*;

import com.onlyoffice.slack.domain.slack.event.registry.SlackSlashCommandHandlerRegistrar;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import com.onlyoffice.slack.shared.utils.LocaleUtils;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.handler.builtin.SlashCommandHandler;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.request.views.ViewsOpenRequest;
import com.slack.api.model.block.LayoutBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackCreateDocumentCommandHandler implements SlackSlashCommandHandlerRegistrar {
  private final MessageSourceSlackConfiguration messageSourceSlackConfiguration;
  private final SlackConfigurationProperties slackConfigurationProperties;
  private final MessageSource messageSource;
  private final LocaleUtils localeUtils;

  private List<LayoutBlock> buildModalBlocks(Locale locale) {
    var blocks = new ArrayList<LayoutBlock>();

    blocks.add(
        input(
            input ->
                input
                    .blockId("document_title")
                    .label(
                        plainText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageCreateDocumentLabel(),
                                null,
                                locale)))
                    .element(
                        plainTextInput(
                            textInput ->
                                textInput
                                    .actionId("document_title_input")
                                    .placeholder(
                                        plainText(
                                            messageSource.getMessage(
                                                messageSourceSlackConfiguration
                                                    .getMessageCreateDocumentPlaceholder(),
                                                null,
                                                locale)))
                                    .initialValue(
                                        messageSource.getMessage(
                                            messageSourceSlackConfiguration
                                                .getMessageCreateDocumentName(),
                                            null,
                                            locale))))));
    blocks.add(
        input(
            input ->
                input
                    .blockId("document_type")
                    .label(
                        plainText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageCreateDocumentTypeLabel(),
                                null,
                                locale)))
                    .element(
                        staticSelect(
                            select ->
                                select
                                    .actionId("document_type_select")
                                    .placeholder(
                                        plainText(
                                            messageSource.getMessage(
                                                messageSourceSlackConfiguration
                                                    .getMessageCreateDocumentTypePlaceholder(),
                                                null,
                                                locale)))
                                    .options(
                                        List.of(
                                            option(
                                                opt ->
                                                    opt.text(
                                                            plainText(
                                                                messageSource.getMessage(
                                                                    messageSourceSlackConfiguration
                                                                        .getMessageCreateDocumentTypeDocument(),
                                                                    null,
                                                                    locale)))
                                                        .value("docx")),
                                            option(
                                                opt ->
                                                    opt.text(
                                                            plainText(
                                                                messageSource.getMessage(
                                                                    messageSourceSlackConfiguration
                                                                        .getMessageCreateDocumentTypeSpreadsheet(),
                                                                    null,
                                                                    locale)))
                                                        .value("xlsx")),
                                            option(
                                                opt ->
                                                    opt.text(
                                                            plainText(
                                                                messageSource.getMessage(
                                                                    messageSourceSlackConfiguration
                                                                        .getMessageCreateDocumentTypePresentation(),
                                                                    null,
                                                                    locale)))
                                                        .value("pptx"))))))));

    return blocks;
  }

  private void openCreateDocumentModal(
      SlashCommandRequest req, SlashCommandContext ctx, Locale locale) throws Exception {
    var blocks = buildModalBlocks(locale);

    ctx.client()
        .viewsOpen(
            ViewsOpenRequest.builder()
                .triggerId(req.getPayload().getTriggerId())
                .view(
                    view(
                        view ->
                            view.type("modal")
                                .callbackId(
                                    slackConfigurationProperties.getCreateDocumentModalActionId())
                                .title(
                                    viewTitle(
                                        title ->
                                            title
                                                .type("plain_text")
                                                .text(
                                                    messageSource.getMessage(
                                                        messageSourceSlackConfiguration
                                                            .getMessageCreateDocumentTitle(),
                                                        null,
                                                        locale))))
                                .submit(
                                    viewSubmit(
                                        submit ->
                                            submit
                                                .type("plain_text")
                                                .text(
                                                    messageSource.getMessage(
                                                        messageSourceSlackConfiguration
                                                            .getMessageCreateDocumentSubmit(),
                                                        null,
                                                        locale))))
                                .blocks(blocks)
                                .privateMetadata(req.getPayload().getChannelId())))
                .build());
  }

  @Override
  public String getSlash() {
    return "/file";
  }

  @Override
  public SlashCommandHandler getHandler() {
    return (req, ctx) -> {
      try {
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

        openCreateDocumentModal(req, ctx, localeUtils.toLocale(lang));

        return ctx.ack();
      } catch (Exception e) {
        log.error("Error opening create document modal", e);
        return ctx.ack();
      }
    };
  }
}
