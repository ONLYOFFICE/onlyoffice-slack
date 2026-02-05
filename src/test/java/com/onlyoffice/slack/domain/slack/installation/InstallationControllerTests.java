/**
 * (c) Copyright Ascensio System SIA 2026
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class InstallationControllerTests {
  @Mock private MessageSourceSlackConfiguration messageSourceSlackConfiguration;
  @Mock private MessageSource messageSource;
  @Mock private Model model;

  private InstallationController controller;

  @BeforeEach
  void setUp() {
    controller = new InstallationController(messageSourceSlackConfiguration, messageSource);
  }

  @Test
  void whenCompletionCalled_thenReturnsCompletionViewAndModel() {
    when(messageSourceSlackConfiguration.getMessageCompletionTitle())
        .thenReturn("completion.title");
    when(messageSourceSlackConfiguration.getMessageCompletionText()).thenReturn("completion.text");
    when(messageSourceSlackConfiguration.getMessageCompletionButton())
        .thenReturn("completion.button");

    when(messageSource.getMessage(eq("completion.title"), any(), eq(Locale.ENGLISH)))
        .thenReturn("Title");
    when(messageSource.getMessage(eq("completion.text"), any(), eq(Locale.ENGLISH)))
        .thenReturn("Text");
    when(messageSource.getMessage(eq("completion.button"), any(), eq(Locale.ENGLISH)))
        .thenReturn("Button");

    var result = controller.completion(model);

    assertEquals("installation/completion", result);
    verify(model).addAttribute("title", "Title");
    verify(model).addAttribute("text", "Text");
    verify(model).addAttribute("button", "Button");
  }

  @Test
  void whenCancellationCalled_thenReturnsCancellationViewAndModel() {
    when(messageSourceSlackConfiguration.getMessageCancellationTitle())
        .thenReturn("cancellation.title");
    when(messageSourceSlackConfiguration.getMessageCancellationText())
        .thenReturn("cancellation.text");
    when(messageSourceSlackConfiguration.getMessageCancellationButton())
        .thenReturn("cancellation.button");

    when(messageSource.getMessage(eq("cancellation.title"), any(), eq(Locale.ENGLISH)))
        .thenReturn("Title");
    when(messageSource.getMessage(eq("cancellation.text"), any(), eq(Locale.ENGLISH)))
        .thenReturn("Text");
    when(messageSource.getMessage(eq("cancellation.button"), any(), eq(Locale.ENGLISH)))
        .thenReturn("Button");

    var result = controller.cancellation(model);

    assertEquals("installation/cancellation", result);
    verify(model).addAttribute("title", "Title");
    verify(model).addAttribute("text", "Text");
    verify(model).addAttribute("button", "Button");
  }
}
