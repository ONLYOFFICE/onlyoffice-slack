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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team_settings")
public class TeamSettings {
  @Id
  @Column(name = "team_id", nullable = false)
  private String teamId;

  @Column(name = "address")
  private String address;

  @Column(name = "header")
  private String header;

  @Column(name = "secret", length = 1000)
  private String secret;

  @Builder.Default
  @Column(name = "demo_enabled", nullable = false)
  private Boolean demoEnabled = false;

  @Column(name = "demo_started_date")
  private LocalDateTime demoStartedDate;
}
