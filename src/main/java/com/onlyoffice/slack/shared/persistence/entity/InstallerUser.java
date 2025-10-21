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
package com.onlyoffice.slack.shared.persistence.entity;

import com.onlyoffice.slack.shared.persistence.converter.EncryptionAttributeConverter;
import com.onlyoffice.slack.shared.persistence.entity.id.InstallerUserId;
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "installer_users",
    indexes = {@Index(name = "idx_team_user", columnList = "team_id,installer_user_id")})
@IdClass(InstallerUserId.class)
public class InstallerUser implements Serializable {
  @Id
  @Column(name = "team_id", nullable = false)
  private String teamId;

  @Id
  @Column(name = "installer_user_id", nullable = false)
  private String installerUserId;

  @Column(name = "app_id", nullable = false)
  private String appId;

  @Column(name = "token_type", nullable = false)
  private String tokenType;

  @Column(name = "installer_user_scope")
  private String installerUserScope;

  @Column(name = "installer_user_access_token", length = 1000)
  @Convert(converter = EncryptionAttributeConverter.class)
  private String installerUserAccessToken;

  @Column(name = "installer_user_refresh_token", length = 1000)
  @Convert(converter = EncryptionAttributeConverter.class)
  private String installerUserRefreshToken;

  @Column(name = "installer_user_token_expires_at")
  private Long installerUserTokenExpiresAt;

  @Column(name = "installed_at", nullable = false)
  private Long installedAt;

  @Column(name = "bot_id")
  private String botId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(
        name = "team_id",
        referencedColumnName = "team_id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "bot_id",
        referencedColumnName = "bot_id",
        insertable = false,
        updatable = false)
  })
  private BotUser bot;

  public void setBot(BotUser bot) {
    if (this.bot != null) this.bot.getUsers().remove(this);
    this.bot = bot;
    if (bot != null) {
      this.botId = bot.getBotId();
      bot.getUsers().add(this);
    } else {
      this.botId = null;
    }
  }
}
