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
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
class InstallationController {
  private final MessageSourceSlackConfiguration messageSourceSlackConfiguration;

  private final MessageSource messageSource;

  @GetMapping(value = "/slack/oauth/completion")
  public String completion(Model model) {
    model.addAttribute(
        "title",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageCompletionTitle(), null, Locale.ENGLISH));
    model.addAttribute(
        "text",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageCompletionText(), null, Locale.ENGLISH));
    model.addAttribute(
        "button",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageCompletionButton(), null, Locale.ENGLISH));

    return "installation/completion";
  }

  @GetMapping(value = "/slack/oauth/cancellation")
  public String cancellation(Model model) {
    model.addAttribute(
        "title",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageCancellationTitle(), null, Locale.ENGLISH));
    model.addAttribute(
        "text",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageCancellationText(), null, Locale.ENGLISH));
    model.addAttribute(
        "button",
        messageSource.getMessage(
            messageSourceSlackConfiguration.getMessageCancellationButton(), null, Locale.ENGLISH));

    return "installation/cancellation";
  }
}
