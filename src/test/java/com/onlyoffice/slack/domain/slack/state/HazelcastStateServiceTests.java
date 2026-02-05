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
package com.onlyoffice.slack.domain.slack.state;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hazelcast.map.IMap;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HazelcastStateServiceTests {
  @Mock private IMap<String, Instant> states;
  @InjectMocks private HazelcastStateService service;

  @Test
  void whenAddNewStateToDatastore_thenStateIsPutIfAbsent() {
    var state = "state";

    service.addNewStateToDatastore(state);

    verify(states, times(1)).putIfAbsent(eq(state), any(Instant.class));
  }

  @Test
  void whenIsAvailableInDatabase_thenReturnTrueIfExists() {
    var state = "state";

    when(states.containsKey(state)).thenReturn(true);

    assertTrue(service.isAvailableInDatabase(state));
  }

  @Test
  void whenIsAvailableInDatabase_thenReturnFalseIfNotExists() {
    var state = "state";

    when(states.containsKey(state)).thenReturn(false);

    assertFalse(service.isAvailableInDatabase(state));
  }

  @Test
  void whenDeleteStateFromDatastore_thenRemoveAsyncCalled() throws Exception {
    var state = "state";

    service.deleteStateFromDatastore(state);

    verify(states, times(1)).removeAsync(state);
  }
}
