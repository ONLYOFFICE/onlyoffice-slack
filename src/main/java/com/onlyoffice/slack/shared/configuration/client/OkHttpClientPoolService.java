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

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OkHttpClientPoolService {
  private final HttpClientConfigurationProperties httpClientProperties;

  private volatile OkHttpClient httpClient;

  public OkHttpClient getHttpClient() {
    if (httpClient == null) {
      synchronized (this) {
        if (httpClient == null) httpClient = createHttpClient();
      }
    }

    return httpClient;
  }

  private OkHttpClient createHttpClient() {
    return new OkHttpClient.Builder()
        .connectTimeout(httpClientProperties.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
        .readTimeout(httpClientProperties.getReadTimeoutSeconds(), TimeUnit.SECONDS)
        .writeTimeout(httpClientProperties.getWriteTimeoutSeconds(), TimeUnit.SECONDS)
        .callTimeout(httpClientProperties.getCallTimeoutSeconds(), TimeUnit.SECONDS)
        .connectionPool(
            new ConnectionPool(
                httpClientProperties.getMaxIdleConnections(),
                httpClientProperties.getKeepAliveDurationMinutes(),
                TimeUnit.MINUTES))
        .retryOnConnectionFailure(httpClientProperties.isRetryOnConnectionFailure())
        .followRedirects(httpClientProperties.isFollowRedirects())
        .followSslRedirects(httpClientProperties.isFollowSslRedirects())
        .addNetworkInterceptor(
            chain -> {
              var response = chain.proceed(chain.request());
              if (response.body() != null) {
                var contentLength = response.body().contentLength();
                if (contentLength > httpClientProperties.getMaxResponseSizeBytes()) {
                  response.close();
                  throw new IOException(
                      "Response size ("
                          + contentLength
                          + " bytes) exceeds maximum allowed ("
                          + httpClientProperties.getMaxResponseSizeBytes()
                          + " bytes)");
                }
              }
              return response;
            })
        .build();
  }
}
