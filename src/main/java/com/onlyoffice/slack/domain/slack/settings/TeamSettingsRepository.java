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
package com.onlyoffice.slack.domain.slack.settings;

import com.onlyoffice.slack.shared.persistence.entity.TeamSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamSettingsRepository extends JpaRepository<TeamSettings, String> {

  @Modifying
  @Query(
      value =
          """
    INSERT INTO team_settings (team_id, address, header, secret, demo_enabled, demo_started_date)
    VALUES (:teamId, :address, :header, :secret, :demoEnabled,
            CASE WHEN :demoEnabled = true THEN CURRENT_TIMESTAMP ELSE NULL END)
    ON CONFLICT (team_id) DO UPDATE SET
      address = :address,
      header = :header,
      secret = :secret,
      demo_enabled = :demoEnabled,
      demo_started_date = CASE
        WHEN :demoEnabled = true AND team_settings.demo_started_date IS NULL
        THEN CURRENT_TIMESTAMP
        ELSE team_settings.demo_started_date
      END
    """,
      nativeQuery = true)
  void upsertSettings(
      @Param("teamId") String teamId,
      @Param("address") String address,
      @Param("header") String header,
      @Param("secret") String secret,
      @Param("demoEnabled") Boolean demoEnabled);
}
