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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyoffice.slack.shared.configuration.client.OkHttpClientPoolService;
import com.onlyoffice.slack.shared.transfer.command.DocumentServerCommand;
import com.onlyoffice.slack.shared.transfer.command.DocumentServerTokenCommand;
import com.onlyoffice.slack.shared.transfer.command.DocumentServerVersion;
import com.onlyoffice.slack.shared.transfer.request.SubmitSettingsRequest;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
class DocumentSettingsValidationServiceImpl implements DocumentSettingsValidationService {
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final OkHttpClientPoolService httpClientPoolService;
  private final DocumentJwtManagerService documentJwtManagerService;

  private String sanitizeAddress(final String address) {
    return address.replaceAll("/+$", "");
  }

  private URL validateConnectionAddress(final String address) throws MalformedURLException {
    var url = URI.create(sanitizeAddress(address)).toURL();
    if (url.getProtocol().equalsIgnoreCase("https")) return url;
    throw new MalformedURLException(
        "Received a malformed connection address. Expected to get https protocol");
  }

  private void validateDocumentServerHealth(final URL address) throws IOException {
    var request = new Request.Builder().url("%s/healthcheck".formatted(address)).build();
    try (var okResponse = httpClientPoolService.getHttpClient().newCall(request).execute()) {
      if (!okResponse.isSuccessful() || okResponse.body() == null)
        throw new IOException("Could not validate document server health");
    }
  }

  private void validateDocumentServerVersion(
      final URL address, final String header, final String secret) throws IOException {
    var command = DocumentServerCommand.builder().c("version").build();
    var token = documentJwtManagerService.createToken(command, secret);
    var commandToken = DocumentServerTokenCommand.builder().token(token).build();

    executeVersionValidationRequest(address, header, token, commandToken);
  }

  private void validateDocumentServerVersionHeader(
      final URL address, final String header, final String secret) throws IOException {
    var command = DocumentServerCommand.builder().c("version").build();
    var token = documentJwtManagerService.createToken(command, secret);

    executeVersionValidationRequest(address, header, "Bearer %s".formatted(token), command);
  }

  private void executeVersionValidationRequest(
      final URL address,
      final String headerName,
      final String headerValue,
      final Object requestBody)
      throws IOException {

    var jsonBody = objectMapper.writeValueAsString(requestBody);
    var request =
        new Request.Builder()
            .url("%s/command?shardkey=%s".formatted(address, UUID.randomUUID()))
            .header(headerName, headerValue)
            .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
            .build();

    try (var response = httpClientPoolService.getHttpClient().newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null)
        throw new IOException("Could not validate document server version");

      var versionResponse =
          objectMapper.readValue(response.body().bytes(), DocumentServerVersion.class);
      if (versionResponse.getError() != 0)
        throw new IOException("Could not receive a non-error response");
    }
  }

  @Override
  @Retry(name = "settings_validation")
  public void validateConnection(@NotNull final SubmitSettingsRequest request) throws IOException {
    try {
      MDC.put("address", request.getAddress());
      MDC.put("header", request.getHeader());
      log.info("Starting document server connection validation");

      if (request.isDemoEnabled() && !request.isValidConfiguration()) {
        log.info("Demo mode enabled without credentials, skipping validation");
        return;
      }

      var url = validateConnectionAddress(request.getAddress());
      log.info("Connection address validated");

      var healthCheckFuture =
          CompletableFuture.runAsync(
              () -> {
                try {
                  validateDocumentServerHealth(url);
                  log.info("Document server health validated");
                } catch (IOException e) {
                  throw new RuntimeException("Health validation failed", e);
                }
              });

      var versionCheckFuture =
          CompletableFuture.runAsync(
              () -> {
                try {
                  validateDocumentServerVersion(url, request.getHeader(), request.getSecret());
                  log.info("Document server version validated");
                } catch (IOException e) {
                  throw new RuntimeException("Version validation failed", e);
                }
              });

      var versionHeaderCheckFuture =
          CompletableFuture.runAsync(
              () -> {
                try {
                  validateDocumentServerVersionHeader(
                      url, request.getHeader(), request.getSecret());
                  log.info("Document server version header validated");
                } catch (IOException e) {
                  throw new RuntimeException("Version header validation failed", e);
                }
              });

      try {
        CompletableFuture.allOf(healthCheckFuture, versionCheckFuture, versionHeaderCheckFuture)
            .get();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IOException("Validation interrupted", e);
      } catch (ExecutionException e) {
        var cause = e.getCause();
        if (cause instanceof RuntimeException && cause.getCause() instanceof IOException)
          throw (IOException) cause.getCause();
        throw new IOException("Validation failed", cause);
      }

      log.info("Document server connection validation completed successfully");
    } finally {
      MDC.clear();
    }
  }
}
