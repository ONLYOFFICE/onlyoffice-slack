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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyoffice.slack.domain.document.DocumentServerConfigurationProperties;
import com.onlyoffice.slack.shared.exception.domain.DocumentTokenValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Base64;
import java.util.Calendar;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@RequiredArgsConstructor
class DocumentJwtManagerServiceImpl implements DocumentJwtManagerService {
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final DocumentServerConfigurationProperties documentServerConfigurationProperties;

  @Override
  public String createToken(@NotNull Object object, @NotBlank String key) {
    var payloadMap = objectMapper.convertValue(object, new TypeReference<Map<String, ?>>() {});
    return createToken(payloadMap, key);
  }

  @Override
  public String createToken(@NotNull Map<String, ?> payloadMap, @NotBlank String key) {
    var algorithm = Algorithm.HMAC256(key);
    var calendar = Calendar.getInstance();
    var issuedAt = calendar.toInstant();
    calendar.add(
        Calendar.MINUTE, documentServerConfigurationProperties.getJwt().getKeepAliveMinutes());

    return JWT.create()
        .withIssuedAt(issuedAt)
        .withExpiresAt(calendar.toInstant())
        .withPayload(payloadMap)
        .sign(algorithm);
  }

  @Override
  public Map<String, Object> verifyToken(@NotBlank String token, @NotBlank String key) {
    try {
      var algorithm = Algorithm.HMAC256(key);
      var decoder = Base64.getUrlDecoder();

      var jwt =
          JWT.require(algorithm)
              .acceptLeeway(
                  documentServerConfigurationProperties.getJwt().getAcceptableLeewaySeconds())
              .build()
              .verify(token);

      var payloadJson = new String(decoder.decode(jwt.getPayload()));
      return objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      throw new DocumentTokenValidationException("Failed to verify and parse JWT token", e);
    }
  }
}
