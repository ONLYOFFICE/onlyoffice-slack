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
import com.onlyoffice.slack.shared.transfer.response.SettingsResponse;
import com.onlyoffice.slack.shared.utils.Mapper;
import org.springframework.stereotype.Component;

@Component
class TeamSettingsMapper implements Mapper<TeamSettings, SettingsResponse> {

  @Override
  public SettingsResponse map(TeamSettings source) {
    if (source == null) {
      return SettingsResponse.builder().build();
    }

    return SettingsResponse.builder()
        .address(source.getAddress())
        .header(source.getHeader())
        .secret(source.getSecret())
        .demoEnabled(source.getDemoEnabled())
        .demoStartedDate(source.getDemoStartedDate())
        .build();
  }
}
