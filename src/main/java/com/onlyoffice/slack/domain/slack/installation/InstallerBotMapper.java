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
import com.slack.api.bolt.model.builtin.DefaultBot;
import org.springframework.stereotype.Component;

@Component
class InstallerBotMapper implements Mapper<InstallerUser, DefaultBot> {

  @Override
  public DefaultBot map(InstallerUser user) {
    if (user == null || user.getBot() == null || user.getBot().getBotAccessToken() == null) {
      return null;
    }

    var bot = new DefaultBot();
    bot.setAppId(user.getAppId());
    bot.setTeamId(user.getTeamId());
    bot.setBotId(user.getBot().getBotId());
    bot.setBotUserId(user.getBot().getBotUserId());
    bot.setBotAccessToken(user.getBot().getBotAccessToken());
    bot.setBotRefreshToken(user.getBot().getBotRefreshToken());
    bot.setBotTokenExpiresAt(user.getBot().getBotTokenExpiresAt());
    bot.setBotScope(user.getBot().getBotScope());
    bot.setTokenType(user.getTokenType());
    bot.setScope(user.getBot().getBotScope());
    bot.setInstalledAt(user.getInstalledAt());

    return bot;
  }
}
