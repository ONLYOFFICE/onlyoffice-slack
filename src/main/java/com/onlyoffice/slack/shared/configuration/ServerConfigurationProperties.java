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
@ConfigurationProperties(prefix = "server")
public class ServerConfigurationProperties {
  @NotBlank private String baseAddress;
  private CryptographyProperties cryptography = new CryptographyProperties();
  private DemoProperties demo = new DemoProperties();

  @Data
  @Builder
  @Validated
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CryptographyProperties {
    @NotBlank private String secret;
  }

  @Data
  @Builder
  @Validated
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DemoProperties {
    @NotBlank private String address;
    @NotBlank private String header;
    @NotBlank private String secret;
    private int durationDays = 7;
  }
}
