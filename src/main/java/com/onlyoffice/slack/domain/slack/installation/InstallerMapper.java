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
package com.onlyoffice.slack.domain.slack.installation;

import com.onlyoffice.slack.shared.persistence.entity.InstallerUser;
import com.onlyoffice.slack.shared.utils.Mapper;
import com.slack.api.bolt.model.builtin.DefaultInstaller;
import org.springframework.stereotype.Component;

@Component
class InstallerMapper implements Mapper<InstallerUser, DefaultInstaller> {

  @Override
  public DefaultInstaller map(InstallerUser user) {
    if (user == null
        || (user.getInstallerUserAccessToken() == null
            && (user.getBot() == null || user.getBot().getBotAccessToken() == null))) {
      return null;
    }

    var builder =
        DefaultInstaller.builder()
            .appId(user.getAppId())
            .teamId(user.getTeamId())
            .installerUserId(user.getInstallerUserId())
            .installerUserScope(user.getInstallerUserScope())
            .installerUserAccessToken(user.getInstallerUserAccessToken())
            .installerUserRefreshToken(user.getInstallerUserRefreshToken())
            .installerUserTokenExpiresAt(user.getInstallerUserTokenExpiresAt())
            .tokenType(user.getTokenType())
            .installedAt(user.getInstalledAt());

    if (user.getBot() != null) {
      builder
          .botId(user.getBot().getBotId())
          .botUserId(user.getBot().getBotUserId())
          .botAccessToken(user.getBot().getBotAccessToken())
          .botRefreshToken(user.getBot().getBotRefreshToken())
          .botTokenExpiresAt(user.getBot().getBotTokenExpiresAt())
          .botScope(user.getBot().getBotScope());
    }

    return builder.build();
  }
}
