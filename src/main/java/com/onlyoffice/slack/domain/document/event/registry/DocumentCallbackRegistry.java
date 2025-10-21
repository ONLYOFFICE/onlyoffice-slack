/**
 * (c) Copyright Ascensio System SIA 2025
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

import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.model.documenteditor.callback.Status;
import com.onlyoffice.slack.shared.utils.TriFunction;
import java.util.Map;
import java.util.Optional;

public interface DocumentCallbackRegistry {
  void register(Status status, TriFunction<String, String, Callback, Callback> callbackHandler);

  Optional<TriFunction<String, String, Callback, Callback>> find(Status status);

  Map<Status, TriFunction<String, String, Callback, Callback>> getRegistry();
}
