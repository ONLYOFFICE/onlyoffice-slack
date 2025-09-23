package com.onlyoffice.slack.domain.slack.event.event;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.view;

import com.onlyoffice.slack.domain.slack.settings.SettingsService;
import com.onlyoffice.slack.shared.configuration.ServerConfigurationProperties;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import com.onlyoffice.slack.shared.exception.domain.DocumentSettingsConfigurationException;
import com.onlyoffice.slack.shared.transfer.response.SettingsResponse;
import com.onlyoffice.slack.shared.utils.LocaleUtils;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.event.AppHomeOpenedEvent;
import java.io.IOException;
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
class SlackAppHomeOpenedEventHandler implements BoltEventHandler<AppHomeOpenedEvent> {
  private final MessageSourceSlackConfiguration messageSourceSlackConfiguration;
  private final ServerConfigurationProperties serverConfigurationProperties;
  private final SlackConfigurationProperties slackConfigurationProperties;

  private final SettingsService settingsService;
  private final MessageSource messageSource;
  private final LocaleUtils localeUtils;

  private List<LayoutBlock> buildSettingsInputs(
      final SettingsResponse settings, final Locale locale) {
    var inputs = new ArrayList<LayoutBlock>();
    inputs.add(
        header(
            h ->
                h.text(
                    plainText(
                        messageSource.getMessage(
                            messageSourceSlackConfiguration.getMessageHomeSettingsTitle(),
                            null,
                            locale)))));
    inputs.add(divider());
    inputs.add(
        input(
            i ->
                i.blockId("https_address")
                    .label(
                        plainText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration
                                    .getMessageHomeInputHttpsAddressLabel(),
                                null,
                                locale)))
                    .element(
                        plainTextInput(
                            pti ->
                                pti.actionId("https_address_input")
                                    .placeholder(
                                        plainText(
                                            messageSource.getMessage(
                                                messageSourceSlackConfiguration
                                                    .getMessageHomeInputHttpsAddressPlaceholder(),
                                                null,
                                                locale)))
                                    .initialValue(
                                        settings == null ? "" : settings.getAddress())))));
    inputs.add(
        input(
            i ->
                i.blockId("secret")
                    .label(
                        plainText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageHomeInputSecretLabel(),
                                null,
                                locale)))
                    .element(
                        plainTextInput(
                            pti ->
                                pti.actionId("secret_input")
                                    .placeholder(
                                        plainText(
                                            messageSource.getMessage(
                                                messageSourceSlackConfiguration
                                                    .getMessageHomeInputSecretPlaceholder(),
                                                null,
                                                locale)))
                                    .initialValue(settings == null ? "" : settings.getSecret())))));
    inputs.add(
        context(
            ctx ->
                ctx.elements(
                    List.of(
                        markdownText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageHomeSecretHelp(),
                                null,
                                locale))))));
    inputs.add(
        input(
            i ->
                i.blockId("header")
                    .label(
                        plainText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageHomeInputHeaderLabel(),
                                null,
                                locale)))
                    .element(
                        plainTextInput(
                            pti ->
                                pti.actionId("header_input")
                                    .placeholder(
                                        plainText(
                                            messageSource.getMessage(
                                                messageSourceSlackConfiguration
                                                    .getMessageHomeInputHeaderPlaceholder(),
                                                null,
                                                locale)))
                                    .initialValue(settings == null ? "" : settings.getHeader())))));
    inputs.add(
        context(
            ctx ->
                ctx.elements(
                    List.of(
                        markdownText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageHomeHeaderHelp(),
                                null,
                                locale))))));
    inputs.add(
        input(
            i ->
                i.blockId("demo_enabled")
                    .label(
                        plainText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration
                                    .getMessageHomeInputDemoSettingsLabel(),
                                null,
                                locale)))
                    .element(
                        checkboxes(
                            c -> {
                              var enableDemoText =
                                  messageSource.getMessage(
                                      messageSourceSlackConfiguration
                                          .getMessageHomeCheckboxEnableDemo(),
                                      null,
                                      locale);
                              var checkboxBuilder =
                                  c.actionId("demo_enabled_checkbox")
                                      .options(
                                          List.of(
                                              option(
                                                  o ->
                                                      o.text(plainText(enableDemoText))
                                                          .value("demo_enabled"))));
                              if (settings != null && settings.isDemoEnabled()) {
                                checkboxBuilder.initialOptions(
                                    List.of(
                                        option(
                                            o ->
                                                o.text(plainText(enableDemoText))
                                                    .value("demo_enabled"))));
                              }
                              return checkboxBuilder;
                            }))));
    inputs.add(
        context(
            ctx ->
                ctx.elements(
                    List.of(
                        markdownText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageHomeDemoHelp(),
                                null,
                                locale))))));

    return inputs;
  }

  private LayoutBlock buildSaveButtonActions(final Locale locale) {
    return actions(
        actions ->
            actions.elements(
                asElements(
                    button(
                        b ->
                            b.text(
                                    plainText(
                                        messageSource.getMessage(
                                            messageSourceSlackConfiguration
                                                .getMessageHomeButtonSaveSettings(),
                                            null,
                                            locale)))
                                .actionId("submit_settings")
                                .style("primary")))));
  }

  private List<LayoutBlock> buildWelcomeBlocks(final Locale locale) {
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(
        header(
            h ->
                h.text(
                    plainText(
                        messageSource.getMessage(
                            messageSourceSlackConfiguration.getMessageHomeWelcomeTitle(),
                            null,
                            locale)))));
    blocks.add(divider());
    blocks.add(
        section(
            s ->
                s.text(
                    markdownText(
                        messageSource.getMessage(
                            messageSourceSlackConfiguration.getMessageHomeWelcomeDescription(),
                            null,
                            locale)))));
    blocks.add(
        actions(
            actions ->
                actions.elements(
                    asElements(
                        button(
                            b ->
                                b.text(
                                        plainText(
                                            messageSource.getMessage(
                                                    messageSourceSlackConfiguration
                                                        .getMessageHomeReadMoreEmoji(),
                                                    null,
                                                    locale)
                                                + " "
                                                + messageSource.getMessage(
                                                    messageSourceSlackConfiguration
                                                        .getMessageHomeReadMore(),
                                                    null,
                                                    locale)))
                                    .actionId(slackConfigurationProperties.getReadMoreActionId())
                                    .url(slackConfigurationProperties.getWelcomeReadMoreUrl())),
                        button(
                            b ->
                                b.text(
                                        plainText(
                                            messageSource.getMessage(
                                                    messageSourceSlackConfiguration
                                                        .getMessageHomeSuggestFeatureEmoji(),
                                                    null,
                                                    locale)
                                                + " "
                                                + messageSource.getMessage(
                                                    messageSourceSlackConfiguration
                                                        .getMessageHomeSuggestFeature(),
                                                    null,
                                                    locale)))
                                    .actionId(
                                        slackConfigurationProperties.getSuggestFeatureActionId())
                                    .url(
                                        slackConfigurationProperties
                                            .getWelcomeSuggestFeatureUrl()))))));
    return blocks;
  }

  private List<LayoutBlock> buildCloudSection(final Locale locale) {
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(
        header(
            h ->
                h.text(
                    plainText(
                        messageSource.getMessage(
                            messageSourceSlackConfiguration.getMessageHomeCloudTitle(),
                            null,
                            locale)))));
    blocks.add(divider());
    blocks.add(
        section(
            s ->
                s.text(
                        markdownText(
                            messageSource.getMessage(
                                messageSourceSlackConfiguration.getMessageHomeCloudDescription(),
                                null,
                                locale)))
                    .accessory(
                        button(
                            b ->
                                b.text(
                                        plainText(
                                            messageSource.getMessage(
                                                messageSourceSlackConfiguration
                                                    .getMessageHomeCloudButton(),
                                                null,
                                                locale)))
                                    .actionId("get_cloud")
                                    .url(slackConfigurationProperties.getGetCloudUrl())))));
    return blocks;
  }

  private List<LayoutBlock> buildHomeTabBlocks(
      final SettingsResponse settings, final Locale locale) {
    var blocks = new ArrayList<>(buildWelcomeBlocks(locale));

    blocks.addAll(buildSettingsInputs(settings, locale));
    blocks.add(buildSaveButtonActions(locale));
    blocks.addAll(buildCloudSection(locale));

    return blocks;
  }

  private void updateHomeTab(
      final AppHomeOpenedEvent event, final EventContext ctx, final Locale locale)
      throws IOException, SlackApiException {
    var settings = SettingsResponse.builder().build();

    try {
      settings = settingsService.findSettings(ctx.getTeamId());
    } catch (DocumentSettingsConfigurationException e) {
      try {
        settings = settingsService.alwaysFindSettings(ctx.getTeamId());
      } catch (Exception fallbackException) {
        settings = SettingsResponse.builder().build();
      }
    }

    var blocks = buildHomeTabBlocks(settings, locale);
    var view = view(v -> v.type("home").blocks(asBlocks(blocks.toArray(new LayoutBlock[0]))));
    var response = ctx.client().viewsPublish(r -> r.userId(event.getUser()).view(view));

    if (!response.isOk()) {
      var errorMessage =
          messageSource.getMessage(
              messageSourceSlackConfiguration.getMessageHomeErrorRenderView(),
              new Object[] {response.getError()},
              locale);
      log.error(errorMessage);
    }
  }

  private List<LayoutBlock> buildInstallationPageBlocks(final Locale locale) {
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(divider());
    blocks.add(section(s -> s.text(markdownText("*Please install the app*"))));
    blocks.add(
        actions(
            actions ->
                actions.elements(
                    asElements(
                        button(
                            b ->
                                b.text(
                                        plainText(
                                            messageSource.getMessage(
                                                messageSourceSlackConfiguration
                                                    .getMessageInstallButton(),
                                                null,
                                                locale)))
                                    .actionId(slackConfigurationProperties.getInstallActionId())
                                    .style("primary")
                                    .url(
                                        serverConfigurationProperties.getBaseAddress()
                                            + "/slack/install"))))));
    blocks.add(divider());
    return blocks;
  }

  private void showInstallationPage(
      final AppHomeOpenedEvent event, final EventContext ctx, final Locale locale)
      throws IOException, SlackApiException {

    var blocks = buildInstallationPageBlocks(locale);
    var view = view(v -> v.type("home").blocks(asBlocks(blocks.toArray(new LayoutBlock[0]))));
    var response = ctx.client().viewsPublish(r -> r.userId(event.getUser()).view(view));

    if (!response.isOk()) {
      var errorMessage = messageSource.getMessage("", new Object[] {response.getError()}, locale);
      log.error(errorMessage);
    }
  }

  @Override
  public Response apply(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) {
    try {
      if (ctx.getRequestUserToken() == null || ctx.getRequestUserToken().isBlank()) {
        var locale = localeUtils.toLocale("en-US");
        showInstallationPage(payload.getEvent(), ctx, locale);
        return ctx.ack();
      }

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

      updateHomeTab(payload.getEvent(), ctx, locale);
      return ctx.ack();
    } catch (IOException | SlackApiException e) {
      log.error("Error updating home tab", e);
      return ctx.ack();
    }
  }
}
