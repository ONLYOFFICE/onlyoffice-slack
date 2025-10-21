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
package com.onlyoffice.slack.shared.persistence.converter;

import com.onlyoffice.slack.shared.utils.AesEncryptionService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Converter
@RequiredArgsConstructor
public class EncryptionAttributeConverter implements AttributeConverter<String, String> {
  private final AesEncryptionService encryptionService;

  @Override
  public String convertToDatabaseColumn(String token) {
    if (token == null || token.trim().isEmpty()) return token;

    return encryptionService.encrypt(token);
  }

  @Override
  public String convertToEntityAttribute(String encToken) {
    if (encToken == null || encToken.trim().isEmpty()) return encToken;

    return encryptionService.decrypt(encToken);
  }
}
