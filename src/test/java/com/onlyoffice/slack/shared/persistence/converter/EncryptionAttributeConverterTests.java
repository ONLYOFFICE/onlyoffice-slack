/**
 * (c) Copyright Ascensio System SIA 2026
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.onlyoffice.slack.shared.utils.AesEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncryptionAttributeConverterTests {
  private AesEncryptionService encryptionService;
  private EncryptionAttributeConverter converter;

  @BeforeEach
  void setUp() {
    encryptionService = mock(AesEncryptionService.class);
    converter = new EncryptionAttributeConverter(encryptionService);
  }

  @Test
  void whenNonNullNonEmptyValueProvidedToConvertToDatabaseColumn_thenEncryptsValue() {
    var plain = "secret";
    var encrypted = "encrypted";

    when(encryptionService.encrypt(plain)).thenReturn(encrypted);
    assertEquals(encrypted, converter.convertToDatabaseColumn(plain));
    verify(encryptionService).encrypt(plain);
  }

  @Test
  void whenNullProvidedToConvertToDatabaseColumn_thenReturnsNull() {
    assertNull(converter.convertToDatabaseColumn(null));
    verifyNoInteractions(encryptionService);
  }

  @Test
  void whenEmptyStringProvidedToConvertToDatabaseColumn_thenReturnsEmptyString() {
    assertEquals("", converter.convertToDatabaseColumn(""));
    verifyNoInteractions(encryptionService);
  }

  @Test
  void whenNonNullNonEmptyValueProvidedToConvertToEntityAttribute_thenDecryptsValue() {
    var encrypted = "encrypted";
    var plain = "secret";

    when(encryptionService.decrypt(encrypted)).thenReturn(plain);
    assertEquals(plain, converter.convertToEntityAttribute(encrypted));
    verify(encryptionService).decrypt(encrypted);
  }

  @Test
  void whenNullProvidedToConvertToEntityAttribute_thenReturnsNull() {
    assertNull(converter.convertToEntityAttribute(null));
    verifyNoInteractions(encryptionService);
  }

  @Test
  void whenEmptyStringProvidedToConvertToEntityAttribute_thenReturnsEmptyString() {
    assertEquals("", converter.convertToEntityAttribute(""));
    verifyNoInteractions(encryptionService);
  }
}
