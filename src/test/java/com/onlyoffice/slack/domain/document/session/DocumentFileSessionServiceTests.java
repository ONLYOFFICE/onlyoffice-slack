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
package com.onlyoffice.slack.domain.document.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.onlyoffice.slack.domain.document.session.entity.ActiveFileSession;
import com.onlyoffice.slack.shared.transfer.cache.DocumentSessionKey;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentFileSessionServiceTests {
  @Mock private DocumentFileSessionRepository documentFileSessionRepository;
  @InjectMocks private DocumentFileSessionService service;

  @Test
  void whenStore_thenRepositorySaveCalled() {
    var fileId = "file_id";
    var session = DocumentSessionKey.builder().key("k").channelId("c").messageTs("m").build();

    service.store(fileId, session);

    verify(documentFileSessionRepository, times(1)).save(any(ActiveFileSession.class));
  }

  @Test
  void whenStoreAll_thenRepositorySaveAllCalled() {
    var map = new HashMap<String, DocumentSessionKey>();
    map.put("file_id", DocumentSessionKey.builder().key("k").channelId("c").messageTs("m").build());

    service.storeAll(map);

    verify(documentFileSessionRepository, times(1)).saveAll(anyList());
  }

  @Test
  void whenDelete_thenRepositoryDeleteByIdCalled() {
    var fileId = "file_id";

    service.delete(fileId);

    verify(documentFileSessionRepository, times(1)).deleteById(fileId);
  }

  @Test
  void whenDeleteAll_thenRepositoryDeleteAllByIdCalled() {
    var ids = Arrays.asList("file_first_id", "file_second_id");

    service.deleteAll(ids);

    verify(documentFileSessionRepository, times(1)).deleteAllById(ids);
  }

  @Test
  void whenLoad_thenReturnSessionKeyIfPresent() {
    var fileId = "file_id";
    var entity =
        ActiveFileSession.builder().fileId(fileId).key("k").channelId("c").messageTs("m").build();

    when(documentFileSessionRepository.findById(fileId)).thenReturn(Optional.of(entity));

    var result = service.load(fileId);

    assertNotNull(result);
    assertEquals("k", result.getKey());
  }

  @Test
  void whenLoad_thenReturnNullIfNotPresent() {
    var fileId = "file_id";

    when(documentFileSessionRepository.findById(fileId)).thenReturn(Optional.empty());

    var result = service.load(fileId);
    assertNull(result);
  }
}
