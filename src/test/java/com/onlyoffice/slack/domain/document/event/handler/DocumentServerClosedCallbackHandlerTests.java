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
package com.onlyoffice.slack.domain.document.event.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import com.hazelcast.map.IMap;
import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.slack.domain.document.editor.core.DocumentFileKeyExtractor;
import com.onlyoffice.slack.shared.transfer.cache.DocumentSessionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServerClosedCallbackHandlerTests {
  @Mock private DocumentFileKeyExtractor documentFileKeyExtractor;
  @Mock private IMap<String, DocumentSessionKey> keys;

  private DocumentClosedCallbackHandler handler;

  @BeforeEach
  void setUp() {
    handler = new DocumentClosedCallbackHandler(documentFileKeyExtractor, keys);
  }

  @Test
  void whenFileIdNull_thenReturnCallback() {
    var callback = mock(Callback.class);

    when(callback.getKey()).thenReturn("key");
    when(documentFileKeyExtractor.extract(anyString(), any())).thenReturn(null);

    var result = handler.getHandler().apply("team", "user", callback);

    assertSame(callback, result);
    verify(keys, never()).remove(any());
  }

  @Test
  void whenUsersEmpty_thenRemoveSessionKey() {
    var callback = mock(Callback.class);
    var fileId = "file";

    when(callback.getKey()).thenReturn("key");
    when(documentFileKeyExtractor.extract(anyString(), any())).thenReturn(fileId);
    when(callback.getUsers()).thenReturn(null);

    var result = handler.getHandler().apply("team", "user", callback);

    assertSame(callback, result);
    verify(keys).remove(fileId);

    reset(keys);
    when(callback.getUsers()).thenReturn(java.util.Collections.emptyList());

    handler.getHandler().apply("team", "user", callback);

    verify(keys).remove(fileId);
  }
}
