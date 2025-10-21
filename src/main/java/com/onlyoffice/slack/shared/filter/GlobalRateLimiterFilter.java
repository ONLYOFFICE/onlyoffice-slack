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
package com.onlyoffice.slack.shared.filter;

import com.onlyoffice.slack.shared.configuration.message.MessageSourceSlackConfiguration;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Integer.MIN_VALUE)
public class GlobalRateLimiterFilter extends OncePerRequestFilter {
  private final MessageSourceSlackConfiguration messageSourceSlackConfiguration;
  private final MessageSource messageSource;

  private final RateLimiter globalRateLimiter;

  @Autowired
  public GlobalRateLimiterFilter(
      MessageSource messageSource,
      MessageSourceSlackConfiguration messageSourceSlackConfiguration) {
    this(
        RateLimiter.of(
            "global",
            RateLimiterConfig.custom()
                .limitForPeriod(2500)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(100))
                .build()),
        messageSource,
        messageSourceSlackConfiguration);
  }

  public GlobalRateLimiterFilter(
      RateLimiter rateLimiter,
      MessageSource messageSource,
      MessageSourceSlackConfiguration messageSourceSlackConfiguration) {
    this.globalRateLimiter = rateLimiter;
    this.messageSource = messageSource;
    this.messageSourceSlackConfiguration = messageSourceSlackConfiguration;
  }

  @Override
  protected void doFilterInternal(
      @NotNull final HttpServletRequest request,
      @NotNull final HttpServletResponse response,
      @NotNull final FilterChain chain)
      throws ServletException, IOException {
    if (!globalRateLimiter.acquirePermission()) {
      response.setStatus(429);
      request.setAttribute(
          "title",
          messageSource.getMessage(
              messageSourceSlackConfiguration.getErrorRateLimiterTitle(), null, Locale.ENGLISH));
      request.setAttribute(
          "text",
          messageSource.getMessage(
              messageSourceSlackConfiguration.getErrorRateLimiterText(), null, Locale.ENGLISH));
      request.setAttribute(
          "action",
          messageSource.getMessage(
              messageSourceSlackConfiguration.getErrorRateLimiterButton(), null, Locale.ENGLISH));
      request.getRequestDispatcher("/error").forward(request, response);
      return;
    }

    chain.doFilter(request, response);
  }
}
