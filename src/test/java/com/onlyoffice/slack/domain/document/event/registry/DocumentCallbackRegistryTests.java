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
package com.onlyoffice.slack.domain.document.event.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.model.documenteditor.callback.Status;
import com.onlyoffice.slack.shared.utils.TriFunction;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class DocumentCallbackRegistryTests {
  @Test
  void whenRegisterAndFindHandler_thenReturnsHandler() {
    var registry = new DocumentCallbackRegistryImpl(Collections.emptyList());
    @SuppressWarnings("unchecked")
    TriFunction<String, String, Callback, Callback> handler = mock(TriFunction.class);

    registry.register(Status.SAVE, handler);
    var found = registry.find(Status.SAVE);

    assertTrue(found.isPresent());
    assertEquals(handler, found.get());
  }

  @Test
  void whenRegisterDuplicateHandler_thenDoesNotOverwrite() {
    var registry = new DocumentCallbackRegistryImpl(Collections.emptyList());
    @SuppressWarnings("unchecked")
    TriFunction<String, String, Callback, Callback> firstHandler = mock(TriFunction.class);
    @SuppressWarnings("unchecked")
    TriFunction<String, String, Callback, Callback> secondHandler = mock(TriFunction.class);

    registry.register(Status.SAVE, firstHandler);
    registry.register(Status.SAVE, secondHandler);
    var found = registry.find(Status.SAVE);

    assertTrue(found.isPresent());
    assertEquals(firstHandler, found.get());
  }
}
