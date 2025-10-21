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
package com.onlyoffice.slack.shared.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "slack.application")
public class SlackConfigurationProperties {
  @NotBlank private String clientId;
  @NotBlank private String clientSecret;
  @NotBlank private String signingSecret;
  @NotBlank private String scopes;
  @NotBlank private String userScopes;
  @NotBlank private String pathInstallation;
  @NotBlank private String pathRedirect;
  @NotBlank private String pathCompletion;
  @NotBlank private String pathCancellation;

  private String createDocumentModalActionId = "create_document_modal";
  private String fileManagerShortcutId = "open_file_manager";
  private String openFileActionId = "open_file";
  private String submitSettingsActionId = "submit_settings";
  private String readMoreActionId = "read_more";
  private String suggestFeatureActionId = "suggest_feature";
  private String getCloudActionId = "get_cloud";
  private String shareFeedbackActionId = "share_feedback";
  private String learnMoreActionId = "learn_more";
  private String installActionId = "install_app";

  private String downloadPathPattern = "%s/download/%s";
  private String editorPathPattern = "%s/editor?session=%s&locale=%s";

  private String welcomeReadMoreUrl = "https://github.com/ONLYOFFICE/onlyoffice-slack";
  private String welcomeSuggestFeatureUrl =
      "https://feedback.onlyoffice.com/forums/966080-your-voice-matters?category_id=519288";
  private String getCloudUrl = "https://www.onlyoffice.com/docs-registration.aspx?referer=slack";
}
