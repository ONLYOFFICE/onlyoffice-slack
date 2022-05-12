package com.onlyoffice.slack.service.slack;

import com.onlyoffice.slack.exception.UnableToPerformSlackOperationException;
import com.onlyoffice.slack.model.slack.Caller;
import com.onlyoffice.slack.model.slack.ScheduledOtp;
import com.onlyoffice.slack.service.registry.SlackOnlyofficeRegistryInstallationService;
import com.slack.api.bolt.App;
import com.slack.api.bolt.model.Installer;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatDeleteScheduledMessageRequest;
import com.slack.api.methods.request.chat.ChatScheduleMessageRequest;
import com.slack.api.methods.request.chat.scheduled_messages.ChatScheduledMessagesListRequest;
import com.slack.api.methods.response.chat.ChatDeleteScheduledMessageResponse;
import com.slack.api.methods.response.chat.ChatScheduleMessageResponse;
import com.slack.api.methods.response.chat.scheduled_messages.ChatScheduledMessagesListResponse;
import com.slack.api.model.block.ContextBlock;
import com.slack.api.model.block.element.ImageElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackOtpGeneratorService {
    private static final String slackBot = "USLACKBOT";
    private static final String msgText = "Unused ONLYOFFICE OTP has expired";
    private static final String altText = "Unused ONLYOFFICE OTP has expired";
    private static final String iconUrl = "https://download.onlyoffice.com/assets/fb/fb_icon_325x325.jpg";

    private final App app;
    private final SlackOnlyofficeRegistryInstallationService installationService;

    public ScheduledOtp generateScheduledOtp(Caller caller) throws UnableToPerformSlackOperationException {
        if (!caller.validate())
            throw new UnableToPerformSlackOperationException("Caller instance is not valid");

        Installer user = installationService.findInstaller(null, caller.getWid(), caller.getId());
        if (user == null)
            throw new UnableToPerformSlackOperationException("Could not fetch caller data");

        log.debug("Generating a new scheduled otp for user: {}", caller.getId());
        Long timestamp = Date.from(LocalDateTime.now().plusHours(12)
                .atZone(ZoneId.systemDefault()).toInstant()).getTime() / 1000;

        try {
            ChatScheduleMessageResponse response = app.client().chatScheduleMessage(ChatScheduleMessageRequest
                    .builder()
                    .postAt(Math.toIntExact(timestamp))
                    .channel(slackBot)
                    .text(altText)
                    .blocks(asBlocks(
                            ContextBlock
                                    .builder()
                                    .elements(List.of(
                                            markdownText(msgText),
                                            ImageElement
                                                    .builder()
                                                    .imageUrl(iconUrl)
                                                    .altText(altText)
                                                    .build()
                                    ))
                                    .build()
                            )
                    )
                    .token(user.getInstallerUserAccessToken())
                    .build());

            if (!response.isOk())
                throw new IOException(response.getError());

            log.debug("Successfully generated an OTP with id = {} and token = {}",
                    response.getScheduledMessageId(), user.getInstallerUserAccessToken());

            return new ScheduledOtp(response.getScheduledMessageId(), response.getChannel(), response.getPostAt());
        } catch (IOException | SlackApiException e) {
            log.warn("An error while generating a new OTP: {}", e.getMessage());
            throw new UnableToPerformSlackOperationException(e.getMessage());
        }
    }

    public boolean validateScheduledOtp(Integer at, String channel, Caller caller) {
        if (at <= 0 || !caller.validate()) return false;

        Installer user = installationService.findInstaller(null, caller.getWid(), caller.getId());
        if (user == null) return false;

        log.debug("Validating a scheduled OTP at {}", at);

        try {
            ChatScheduledMessagesListResponse response = app.client()
                    .chatScheduledMessagesList(ChatScheduledMessagesListRequest
                            .builder()
                            .token(user.getInstallerUserAccessToken())
                            .channel(channel)
                            .limit(1)
                            .latest(at.toString())
                            .oldest(at.toString())
                            .build()
                    );

            if (!response.isOk())
                throw new IOException(response.getError());

            return response.getScheduledMessages().size() == 1;
        } catch (IOException | SlackApiException e) {
            log.warn("An error while checking an OTP {}", e.getMessage());
            return false;
        }
    }

    public boolean removeScheduledOtp(String code, Caller caller) {
        if (code == null || code.isBlank() || !caller.validate()) return false;

        if (caller.getToken() != null && !caller.getToken().isBlank())
            return doRemoveOtp(code, caller.getToken());

        Installer user = installationService.findInstaller(null, caller.getWid(), caller.getId());
        if (user == null) return false;
        return doRemoveOtp(code, user.getInstallerUserAccessToken());
    }

    private boolean doRemoveOtp(String code, String token) {
        log.debug("Removing a scheduled OTP with id = {} and token = {} ", code, token);
        try {
            ChatDeleteScheduledMessageResponse response = app.client()
                    .chatDeleteScheduledMessage(
                            ChatDeleteScheduledMessageRequest
                                    .builder()
                                    .scheduledMessageId(code)
                                    .channel(slackBot)
                                    .token(token)
                                    .build()
                    );

            if (!response.isOk())
                throw new IOException(response.getError());

            log.debug("Successfully removed {}", code);
            return true;
        } catch (IOException | SlackApiException e) {
            log.warn("An error while removing an OTP {}", e.getMessage());
            return false;
        }
    }
}
