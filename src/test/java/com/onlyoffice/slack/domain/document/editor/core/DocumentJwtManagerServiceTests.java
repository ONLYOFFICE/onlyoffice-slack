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
package com.onlyoffice.slack.domain.document.editor.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.onlyoffice.slack.domain.document.DocumentServerConfigurationProperties;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentJwtManagerServiceTests {
  private DocumentJwtManagerService documentJwtManagerService;

  @BeforeEach
  void setUp() {
    var properties = mock(DocumentServerConfigurationProperties.class);
    var jwtProps = mock(DocumentServerConfigurationProperties.JwtProperties.class);

    when(properties.getJwt()).thenReturn(jwtProps);
    when(jwtProps.getKeepAliveMinutes()).thenReturn(10);
    when(jwtProps.getAcceptableLeewaySeconds()).thenReturn(5);

    documentJwtManagerService = new DocumentJwtManagerServiceImpl(properties);
  }

  @Test
  void whenCreateTokenWithValidInput_thenTokenIsCreated() {
    var payload = Map.of("foo", "bar");
    var token = documentJwtManagerService.createToken(payload, "secret");

    assertNotNull(token);
  }

  @Test
  void whenVerifyTokenWithValidToken_thenReturnPayload() {
    var payload = Map.of("foo", "bar");
    var token = documentJwtManagerService.createToken(payload, "secret");
    var result = documentJwtManagerService.verifyToken(token, "secret");

    assertEquals("bar", result.get("foo"));
  }

  @Test
  void whenVerifyTokenWithInvalidToken_thenThrowException() {
    assertThrows(
        Exception.class, () -> documentJwtManagerService.verifyToken("invalid.token", "secret"));
  }
}
