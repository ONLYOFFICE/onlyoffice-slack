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
package com.onlyoffice.slack.shared.configuration.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Builder
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "http.client")
public class HttpClientConfigurationProperties {
  private int connectTimeoutSeconds = 15;
  private int readTimeoutSeconds = 60;
  private int writeTimeoutSeconds = 60;
  private int callTimeoutSeconds = 120;
  private int maxIdleConnections = 20;
  private int keepAliveDurationMinutes = 5;
  private long maxResponseSizeBytes = 25 * 1024 * 1024;
  private boolean retryOnConnectionFailure = true;
  private boolean followRedirects = false;
  private boolean followSslRedirects = false;
}
