package com.onlyoffice.slack.domain.slack.event.action;

import com.onlyoffice.slack.domain.document.editor.core.DocumentSettingsValidationService;
import com.onlyoffice.slack.domain.slack.event.registry.SlackBlockActionHandlerRegistrar;
import com.onlyoffice.slack.domain.slack.settings.SettingsService;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import com.onlyoffice.slack.shared.transfer.request.SubmitSettingsRequest;
import com.onlyoffice.slack.shared.utils.HttpUtils;
import com.onlyoffice.slack.shared.utils.LocaleUtils;
import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostEphemeralRequest;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.model.view.ViewState;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class SlackSubmitSettingsBlockActionHandler implements SlackBlockActionHandlerRegistrar {
  private final SlackConfigurationProperties slackConfigurationProperties;
  private final MessageSourceSlackConfiguration messageSourceSlackConfiguration;

  private final DocumentSettingsValidationService documentSettingsValidationService;
  private final SettingsService settingsService;
  private final MessageSource messageSource;
  private final LocaleUtils localeUtils;
  private final HttpUtils httpUtils;

  private String extractValue(
      final Map<String, Map<String, ViewState.Value>> values,
      final String blockId,
      final String actionId) {
    try {
      return Optional.ofNullable(values.get(blockId))
          .map(blockValues -> blockValues.get(actionId))
          .map(ViewState.Value::getValue)
          .map(String::trim)
          .filter(value -> !value.isEmpty())
          .orElse("");
    } catch (Exception e) {
      log.warn(
          "Error extracting value for block '{}' action '{}': {}",
          blockId,
          actionId,
          e.getMessage());
      return null;
    }
  }

  private boolean extractDemoEnabled(final Map<String, Map<String, ViewState.Value>> values) {
    return Optional.ofNullable(values.get("demo_enabled"))
        .map(block -> block.get("demo_enabled_checkbox"))
        .map(ViewState.Value::getSelectedOptions)
        .map(options -> !options.isEmpty())
        .orElse(false);
  }

  private SubmitSettingsRequest buildSettingsRequest(
      final String httpsAddress, final String secret, final String header, boolean demoEnabled) {
    return SubmitSettingsRequest.builder()
        .address(httpsAddress)
        .secret(secret)
        .header(header)
        .demoEnabled(demoEnabled)
        .build();
  }

  private boolean shouldValidateConnection(final SubmitSettingsRequest request) {
    return !request.isDemoEnabled() || request.isCredentialsComplete();
  }

  private void showSuccessNotification(
      final BlockActionRequest req, final Context ctx, final Locale locale)
      throws IOException, SlackApiException {
    var message =
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageSettingsSuccess(), null, locale);
    ctx.client()
        .chatPostEphemeral(
            ChatPostEphemeralRequest.builder()
                .channel(req.getPayload().getUser().getId())
                .user(req.getPayload().getUser().getId())
                .text(message)
                .build());
  }

  private void showErrorNotification(
      final BlockActionRequest req, final Context ctx, final Locale locale)
      throws IOException, SlackApiException {
    var message =
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageSettingsError(), null, locale);
    ctx.client()
        .chatPostEphemeral(
            ChatPostEphemeralRequest.builder()
                .channel(req.getPayload().getUser().getId())
                .user(req.getPayload().getUser().getId())
                .text(message)
                .build());
  }

  @Override
  public List<String> getIds() {
    return List.of(slackConfigurationProperties.getSubmitSettingsActionId());
  }

  @Override
  public BlockActionHandler getAction() {
    return (req, ctx) -> {
      var values = req.getPayload().getView().getState().getValues();
      var httpsAddress = extractValue(values, "https_address", "https_address_input");
      var secret = extractValue(values, "secret", "secret_input");
      var header = extractValue(values, "header", "header_input");
      var demoEnabled = extractDemoEnabled(values);

      var lang = "en-US";
      try {
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
      } catch (Exception e) {
        log.warn("Could not get user locale, using default: {}", e.getMessage());
      }

      var locale = localeUtils.toLocale(lang);

      try {
        var request =
            buildSettingsRequest(
                httpUtils.normalizeAddress(httpsAddress), secret, header, demoEnabled);

        if (!request.isValidConfiguration()) {
          log.warn(
              "Invalid configuration provided: address={}, secret={}, header={}, demoEnabled={}",
              httpsAddress,
              secret != null ? "***" : null,
              header,
              demoEnabled);
          showErrorNotification(req, ctx, locale);
          return ctx.ack();
        }

        if (shouldValidateConnection(request))
          documentSettingsValidationService.validateConnection(request);

        settingsService.saveSettings(ctx, request);
        showSuccessNotification(req, ctx, locale);
      } catch (Exception e) {
        log.error("Error updating settings", e);
        showErrorNotification(req, ctx, locale);
      }

      return ctx.ack();
    };
  }
}
