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
package com.onlyoffice.slack.shared.persistence.repository;

import com.onlyoffice.slack.shared.persistence.entity.InstallerUser;
import com.onlyoffice.slack.shared.persistence.entity.id.InstallerUserId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InstallerUserRepository extends JpaRepository<InstallerUser, InstallerUserId> {
  Optional<InstallerUser> findFirstByTeamId(String teamId);

  Optional<InstallerUser> findByTeamIdAndInstallerUserId(String teamId, String installerUserId);

  @Query("SELECT u FROM InstallerUser u WHERE u.bot.botUserId = :botUserId")
  Optional<InstallerUser> findByBotUserId(String botUserId);

  @Query("SELECT u FROM InstallerUser u WHERE u.bot.botId = :botId")
  List<InstallerUser> findByBotId(String botId);

  int deleteAllByTeamId(String teamId);
}
