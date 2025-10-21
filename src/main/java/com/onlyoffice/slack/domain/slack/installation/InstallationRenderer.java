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

import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import com.slack.api.bolt.service.builtin.oauth.view.OAuthInstallPageRenderer;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Configuration
@RequiredArgsConstructor
class InstallationRenderer implements OAuthInstallPageRenderer {
  private final MessageSourceSlackConfiguration messageSourceSlackConfiguration;

  private final SpringTemplateEngine engine;
  private final MessageSource messageSource;

  public String render(final String installationUrl) {
    var ctx = new Context();
    ctx.setVariable(
        "title",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageInstallTitle(), null, Locale.ENGLISH));
    ctx.setVariable(
        "subtitle",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageInstallSubtitle(), null, Locale.ENGLISH));
    ctx.setVariable(
        "button",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageInstallButton(), null, Locale.ENGLISH));
    ctx.setVariable(
        "featureEdit",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageInstallFeatureEdit(), null, Locale.ENGLISH));
    ctx.setVariable(
        "featureCollaboration",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageInstallFeatureCollaboration(),
            null,
            Locale.ENGLISH));
    ctx.setVariable(
        "featureFile",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageInstallFeatureFileSupport(),
            null,
            Locale.ENGLISH));
    ctx.setVariable(
        "featureSecure",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageInstallFeatureSecureEditing(),
            null,
            Locale.ENGLISH));
    ctx.setVariable("url", installationUrl);

    return engine.process("installation/install", ctx);
  }
}
