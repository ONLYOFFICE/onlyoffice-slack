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
package com.onlyoffice.slack.domain.slack.event.action;

import com.onlyoffice.slack.domain.slack.event.registry.SlackBlockActionHandlerRegistrar;
import com.onlyoffice.slack.shared.configuration.SlackConfigurationProperties;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class SlackAutoAckBlockActionHandler implements SlackBlockActionHandlerRegistrar {
  private final SlackConfigurationProperties slackConfigurationProperties;

  @Override
  public List<String> getIds() {
    return List.of(
        slackConfigurationProperties.getGetCloudActionId(),
        slackConfigurationProperties.getReadMoreActionId(),
        slackConfigurationProperties.getSuggestFeatureActionId(),
        slackConfigurationProperties.getLearnMoreActionId(),
        slackConfigurationProperties.getShareFeedbackActionId(),
        slackConfigurationProperties.getInstallActionId());
  }

  @Override
  public BlockActionHandler getAction() {
    return (req, ctx) -> ctx.ack();
  }
}
