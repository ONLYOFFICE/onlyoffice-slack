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
package com.onlyoffice.slack.domain.document.editor.core;

import com.onlyoffice.model.common.User;
import com.onlyoffice.model.documenteditor.Config;
import com.onlyoffice.model.documenteditor.config.EditorConfig;
import com.onlyoffice.slack.shared.configuration.ServerConfigurationProperties;
import com.onlyoffice.slack.shared.transfer.command.BuildConfigCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
class DocumentConfigManagerServiceImpl implements DocumentConfigManagerService {
  private final ServerConfigurationProperties serverConfigurationProperties;

  private final DocumentJwtManagerService documentJwtManagerService;
  private final DocumentFileManagerService documentManagerService;

  private String getUserName(com.slack.api.model.User user) {
    var profile = user.getProfile();
    if (profile != null) {
      if (profile.getDisplayName() != null && !profile.getDisplayName().isBlank())
        return profile.getDisplayName();
      if (profile.getRealName() != null && !profile.getRealName().isBlank())
        return profile.getRealName();
    }

    if (user.getRealName() != null && !user.getRealName().isBlank()) return user.getRealName();

    return user.getName();
  }

  @Override
  public Config createConfig(@Valid final BuildConfigCommand command) {
    var user = command.getUser();
    var file = command.getFile();
    var config =
        Config.builder()
            .width("100%")
            .height("100%")
            .type(command.getType())
            .documentType(documentManagerService.getDocumentType(file))
            .document(
                documentManagerService.getDocument(
                    user.getTeamId(),
                    user.getId(),
                    file,
                    command.getChannelId(),
                    command.getMessageTs()))
            .editorConfig(
                EditorConfig.builder()
                    .mode(command.getMode())
                    .lang(command.getLocale() != null ? command.getLocale() : "en-US")
                    .user(
                        User.builder()
                            .id(user.getId())
                            .name(getUserName(user))
                            .image(user.getProfile().getImage32())
                            .build())
                    .callbackUrl(
                        "%s/callback?file=%s"
                            .formatted(
                                serverConfigurationProperties.getBaseAddress(), file.getId()))
                    .build())
            .build();

    config.setToken(documentJwtManagerService.createToken(config, command.getSigningSecret()));

    return config;
  }
}
