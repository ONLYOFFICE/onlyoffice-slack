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

import com.onlyoffice.slack.domain.document.DocumentServerFormatsConfiguration;
import com.onlyoffice.slack.domain.slack.event.registry.SlackViewSubmissionActionRegistrar;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.onlyoffice.slack.shared.exception.domain.DocumentCallbackException;
import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.files.FilesUploadV2Request;
import com.slack.api.model.view.ViewState;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackSubmitCreateDocumentActionHandler implements SlackViewSubmissionActionRegistrar {
  private static final List<String> SUPPORTED_TEMPLATE_TYPES = List.of("docx", "xlsx", "pptx");
  private static final String TEMPLATE_PATH_FORMAT = "document-templates/default/new.%s";
  private static final int MAX_FILENAME_LENGTH = 200;

  private final SlackConfigurationProperties slackConfigurationProperties;
  private final DocumentServerFormatsConfiguration documentServerFormatsConfiguration;

  private record DocumentCreationRequest(String title, String type, String channelId) {}

  private String extractValue(
      Map<String, Map<String, ViewState.Value>> values, String blockId, String actionId) {
    try {
      return Optional.ofNullable(values.get(blockId))
          .map(blockValues -> blockValues.get(actionId))
          .map(this::extractStringValue)
          .orElse(null);
    } catch (Exception e) {
      log.warn("Error extracting value for block '{}' action '{}'", blockId, actionId, e);
      return null;
    }
  }

  private String extractStringValue(ViewState.Value valueObject) {
    var textValue =
        Optional.ofNullable(valueObject.getValue())
            .map(String::trim)
            .filter(v -> !v.isEmpty())
            .orElse(null);

    if (textValue != null) return textValue;

    return Optional.ofNullable(valueObject.getSelectedOption())
        .map(ViewState.SelectedOption::getValue)
        .map(String::trim)
        .filter(v -> !v.isEmpty())
        .orElse(null);
  }

  private DocumentCreationRequest extractDocumentRequest(ViewSubmissionRequest req) {
    var values = req.getPayload().getView().getState().getValues();

    var title = extractValue(values, "document_title", "document_title_input");
    var type = extractValue(values, "document_type", "document_type_select");
    var channelId = req.getPayload().getView().getPrivateMetadata();

    return new DocumentCreationRequest(title, type, channelId);
  }

  private void validateNotEmpty(String value, String errorMessage) throws IOException {
    if (value == null || value.trim().isEmpty()) throw new IOException(errorMessage);
  }

  private void validateSupportedDocumentType(String type) throws IOException {
    if (!SUPPORTED_TEMPLATE_TYPES.contains(type))
      throw new IOException(
          "Unsupported document type: " + type + ". Supported types: " + SUPPORTED_TEMPLATE_TYPES);
  }

  private void validateDocumentServerSupport(String type) throws IOException {
    var isFormatSupported =
        documentServerFormatsConfiguration.getFormats().stream()
            .anyMatch(format -> format.getName().equals(type));
    if (!isFormatSupported)
      throw new IOException("Document type " + type + " is not supported by Document Server");
  }

  private void validateDocumentRequest(DocumentCreationRequest request) throws IOException {
    validateNotEmpty(request.title(), "Document title is required");
    validateNotEmpty(request.type(), "Document type is required");
    validateNotEmpty(request.channelId(), "Channel ID is required");
    validateSupportedDocumentType(request.type());
    validateDocumentServerSupport(request.type());
  }

  private byte[] loadTemplateBytes(String type) throws IOException {
    var templatePath = String.format(TEMPLATE_PATH_FORMAT, type);
    try (var templateStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
      if (templateStream == null)
        throw new IOException(
            "Could not find template file for type: " + type + ". Expected path: " + templatePath);

      log.info("Using template file for type: {}", type);
      return templateStream.readAllBytes();
    }
  }

  private FilesUploadV2Request buildUploadRequest(
      ViewSubmissionContext ctx, String fileName, String channelId, byte[] fileData) {
    return FilesUploadV2Request.builder()
        .token(ctx.getRequestUserToken())
        .filename(fileName)
        .title(fileName)
        .channel(channelId)
        .fileData(fileData)
        .build();
  }

  private void uploadTemplateFile(
      ViewSubmissionContext ctx, String fileName, String type, String channelId)
      throws IOException, SlackApiException {
    log.info("Starting upload to Slack for file: {} with type: {}", fileName, type);

    var buffer = loadTemplateBytes(type);

    var uploadResponse =
        ctx.client().filesUploadV2(buildUploadRequest(ctx, fileName, channelId, buffer));

    if (!uploadResponse.isOk())
      throw new DocumentCallbackException(
          "Failed to upload file to Slack: " + uploadResponse.getError());

    log.info("Successfully uploaded file {} to Slack channel {}", fileName, channelId);
  }

  private void handleDocumentCreation(ViewSubmissionRequest req, ViewSubmissionContext ctx)
      throws IOException, SlackApiException {
    var request = extractDocumentRequest(req);

    validateDocumentRequest(request);

    var fileName = createSanitizedFileName(request.title(), request.type());

    log.info(
        "Creating document: title='{}', type='{}', channel='{}'",
        request.title(),
        request.type(),
        request.channelId());

    uploadTemplateFile(ctx, fileName, request.type(), request.channelId());
  }

  private String createSanitizedFileName(String title, String type) {
    var sanitized =
        title
            .replaceAll("[/\\\\:*?\"<>|\\p{Cntrl}]", "_")
            .replaceAll("^\\.*", "")
            .replaceAll("\\.*$", "")
            .replaceAll("\\s+", " ")
            .trim();

    if (sanitized.isEmpty()) sanitized = "New File";

    int maxBaseLength = MAX_FILENAME_LENGTH - type.length() - 1;
    if (sanitized.length() > maxBaseLength) sanitized = sanitized.substring(0, maxBaseLength);

    return String.format("%s.%s", sanitized, type);
  }

  @Override
  public List<String> getIds() {
    return List.of(slackConfigurationProperties.getCreateDocumentModalActionId());
  }

  @Override
  public ViewSubmissionHandler getAction() {
    return (req, ctx) -> {
      try {
        handleDocumentCreation(req, ctx);
        return ctx.ack();
      } catch (Exception e) {
        return ctx.ackWithErrors(
            Map.of("document_title", "Failed to create document: " + e.getMessage()));
      }
    };
  }
}
