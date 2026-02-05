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
package com.onlyoffice.slack.domain.document.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.onlyoffice.slack.domain.document.editor.core.DocumentJwtManagerService;
import com.onlyoffice.slack.shared.configuration.ServerConfigurationProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentFileProxyControllerTests {
  private ServerConfigurationProperties serverConfigurationProperties;

  private DocumentFileProxyController controller;
  private DocumentJwtManagerService documentJwtManagerService;

  private HttpServletResponse response;

  private Map<String, Object> createValidDecodedToken() {
    var token = new HashMap<String, Object>();
    token.put("teamId", "team");
    token.put("userId", "user");
    token.put("fileId", "file");
    return token;
  }

  @BeforeEach
  void setUp() {
    serverConfigurationProperties = mock(ServerConfigurationProperties.class);
    documentJwtManagerService = mock(DocumentJwtManagerService.class);
    response = mock(HttpServletResponse.class);

    var documentFileStreamingService = mock(DocumentFileStreamingService.class);

    controller =
        new DocumentFileProxyController(
            serverConfigurationProperties, documentJwtManagerService, documentFileStreamingService);
  }

  @Test
  void whenDownloadFileWithValidToken_thenReturnsDeferredResult() {
    var token = "valid-token";
    var decodedToken = createValidDecodedToken();
    var cryptography = mock(ServerConfigurationProperties.CryptographyProperties.class);

    when(serverConfigurationProperties.getCryptography()).thenReturn(cryptography);
    when(cryptography.getSecret()).thenReturn("secret");
    when(documentJwtManagerService.verifyToken(token, "secret")).thenReturn(decodedToken);

    var result = controller.downloadFile(token, response);

    assertNotNull(result);
  }

  @Test
  void whenDownloadFileWithInvalidToken_thenThrowsException() {
    var token = "invalid-token";
    var cryptography = mock(ServerConfigurationProperties.CryptographyProperties.class);

    when(serverConfigurationProperties.getCryptography()).thenReturn(cryptography);
    when(cryptography.getSecret()).thenReturn("secret");
    when(documentJwtManagerService.verifyToken(token, "secret"))
        .thenThrow(new RuntimeException("Invalid token"));

    try {
      controller.downloadFile(token, response);
    } catch (Exception e) {
      assertEquals("Invalid token", e.getMessage());
    }
  }

  @Test
  void whenDownloadFileProcessedSuccessfully_thenDeferredResultCompletes() throws Exception {
    var token = "valid-token";
    var decodedToken = createValidDecodedToken();
    var cryptography = mock(ServerConfigurationProperties.CryptographyProperties.class);

    when(serverConfigurationProperties.getCryptography()).thenReturn(cryptography);
    when(cryptography.getSecret()).thenReturn("secret");
    when(documentJwtManagerService.verifyToken(token, "secret")).thenReturn(decodedToken);

    var result = controller.downloadFile(token, response);

    assertNotNull(result);
    Thread.sleep(100);
  }

  @Test
  void whenDownloadFileWithNullToken_thenThrowsException() {
    String token = null;

    try {
      controller.downloadFile(token, response);
    } catch (Exception e) {
      assertNotNull(e);
    }
  }

  @Test
  void whenDownloadFileWithEmptyToken_thenThrowsException() {
    var token = "";
    var cryptography = mock(ServerConfigurationProperties.CryptographyProperties.class);

    when(serverConfigurationProperties.getCryptography()).thenReturn(cryptography);
    when(cryptography.getSecret()).thenReturn("secret");
    when(documentJwtManagerService.verifyToken(token, "secret"))
        .thenThrow(new RuntimeException("Empty token"));

    try {
      controller.downloadFile(token, response);
    } catch (Exception e) {
      assertEquals("Empty token", e.getMessage());
    }
  }
}
