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
package com.onlyoffice.slack.domain.document.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.slack.domain.document.editor.core.DocumentCallbackService;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DocumentCallbackControllerTests {
  private DocumentCallbackController controller;
  private DocumentCallbackService documentCallbackService;

  @BeforeEach
  void setUp() {
    documentCallbackService = mock(DocumentCallbackService.class);
    controller = new DocumentCallbackController(documentCallbackService);
  }

  @Test
  void whenCallbackProcessedSuccessfully_thenReturnsSuccessResponse() {
    var headers = new HashMap<String, String>();
    headers.put("Authorization", "Bearer token");

    var callback = mock(Callback.class);

    var result = controller.callback(headers, callback);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    Assertions.assertNotNull(result.getBody());
    assertEquals(0, result.getBody().getError());
    verify(documentCallbackService).processCallback(headers, callback);
  }

  @Test
  void whenCallbackProcessingThrowsException_thenReturnsErrorResponse() {
    var headers = new HashMap<String, String>();

    var callback = mock(Callback.class);

    doThrow(new RuntimeException("Processing failed"))
        .when(documentCallbackService)
        .processCallback(headers, callback);

    var result = controller.callback(headers, callback);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    Assertions.assertNotNull(result.getBody());
    assertEquals(1, result.getBody().getError());
    verify(documentCallbackService).processCallback(headers, callback);
  }

  @Test
  void whenCallbackWithEmptyHeaders_thenStillProcessesCallback() {
    var emptyHeaders = new HashMap<String, String>();

    var callback = mock(Callback.class);

    var result = controller.callback(emptyHeaders, callback);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    Assertions.assertNotNull(result.getBody());
    assertEquals(0, result.getBody().getError());
    verify(documentCallbackService).processCallback(emptyHeaders, callback);
  }
}
