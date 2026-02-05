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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.onlyoffice.slack.domain.slack.installation.RotatingInstallationService;
import com.onlyoffice.slack.shared.configuration.client.OkHttpClientPoolService;
import com.onlyoffice.slack.shared.exception.FileContentLengthException;
import com.slack.api.bolt.App;
import com.slack.api.model.File;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentFileStreamingServiceImplTests {
  @Mock private App app;
  @Mock private RotatingInstallationService installationService;
  @Mock private OkHttpClientPoolService httpClientPoolService;

  @Mock private HttpServletResponse response;

  @InjectMocks private DocumentFileStreamingServiceImpl documentFileStreamingService;

  private void invokeValidateFileSize(File file) {
    try {
      var method =
          DocumentFileStreamingServiceImpl.class.getDeclaredMethod("validateFileSize", File.class);
      method.setAccessible(true);
      method.invoke(documentFileStreamingService, file);
    } catch (Exception e) {
      if (e.getCause() instanceof RuntimeException) throw (RuntimeException) e.getCause();
      throw new RuntimeException(e);
    }
  }

  private void invokeSetResponseHeaders(HttpServletResponse response, File file) {
    try {
      var method =
          DocumentFileStreamingServiceImpl.class.getDeclaredMethod(
              "setResponseHeaders", HttpServletResponse.class, File.class);
      method.setAccessible(true);
      method.invoke(documentFileStreamingService, response, file);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void whenFileSizeExceedsMax_thenThrowFileSizeException() {
    var file = new File();
    file.setSize(20 * 1024 * 1024);

    var exception =
        assertThrows(
            FileContentLengthException.class,
            () -> {
              invokeValidateFileSize(file);
            });

    assertTrue(exception.getMessage().contains("exceeds maximum allowed size"));
  }

  @Test
  void whenFileSizeIsZero_thenThrowFileSizeException() {
    var file = new File();
    file.setSize(0);

    var exception =
        assertThrows(
            FileContentLengthException.class,
            () -> {
              invokeValidateFileSize(file);
            });

    assertTrue(exception.getMessage().contains("less or equal to 0 bytes"));
  }

  @Test
  void whenSetResponseHeaders_thenHeadersAreSetCorrectly() {
    var file = new File();
    file.setMimetype("application/pdf");
    file.setSize(1234);
    file.setName("test.pdf");

    invokeSetResponseHeaders(response, file);

    verify(response).setContentType("application/pdf");
    verify(response).setContentLength(1234);
    verify(response).setHeader(eq("Content-Disposition"), contains("test.pdf"));
    verify(response).setHeader(eq("Cache-Control"), anyString());
    verify(response).setHeader(eq("Pragma"), anyString());
    verify(response).setHeader(eq("Expires"), anyString());
  }
}
