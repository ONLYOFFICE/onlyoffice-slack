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
package com.onlyoffice.slack.integration;

import java.time.Duration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Transactional
@SpringBootTest
@Testcontainers
@ActiveProfiles("integration")
public abstract class BaseIntegrationTest {

  @Container
  protected static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("integration_test_db")
          .withUsername("test_user")
          .withPassword("test_password")
          .withReuse(true)
          .withStartupTimeout(Duration.ofMinutes(2))
          .withConnectTimeoutSeconds(60);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

    registry.add("spring.datasource.hikari.maximum-pool-size", () -> "5");
    registry.add("spring.datasource.hikari.minimum-idle", () -> "1");
    registry.add("spring.datasource.hikari.connection-timeout", () -> "10000");
    registry.add("spring.datasource.hikari.idle-timeout", () -> "30000");
    registry.add("spring.datasource.hikari.max-lifetime", () -> "60000");
    registry.add("spring.datasource.hikari.leak-detection-threshold", () -> "30000");

    registry.add("hazelcast.enabled", () -> "false");
    registry.add("spring.cache.type", () -> "simple");
  }
}
