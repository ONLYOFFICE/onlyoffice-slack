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
package com.onlyoffice.slack.domain.slack.event.registry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class SlackBlockActionRegistryTests {
  @Test
  void whenRegisteringHandler_thenHandlerCanBeFound() {
    var registry = new SlackBlockActionRegistryImpl(Collections.emptyList());
    var handler = mock(BlockActionHandler.class);

    registry.register("firstCallback", handler);
    var found = registry.find("firstCallback");

    assertTrue(found.isPresent());
    assertEquals(handler, found.get());
  }

  @Test
  void whenRegisteringDuplicateHandler_thenOriginalHandlerIsNotOverwritten() {
    var registry = new SlackBlockActionRegistryImpl(Collections.emptyList());
    var firstHandler = mock(BlockActionHandler.class);
    var secondHandler = mock(BlockActionHandler.class);

    registry.register("firstCallback", firstHandler);
    registry.register("secondCallback", secondHandler);
    var found = registry.find("firstCallback");

    assertTrue(found.isPresent());
    assertEquals(firstHandler, found.get());
  }

  @Test
  void whenFindingUnregisteredHandler_thenEmptyIsReturned() {
    var registry = new SlackBlockActionRegistryImpl(Collections.emptyList());
    var found = registry.find("not_registered");

    assertTrue(found.isEmpty());
  }
}
