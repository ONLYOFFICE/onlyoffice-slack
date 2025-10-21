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
package com.onlyoffice.slack.domain.document.session;

import com.onlyoffice.slack.domain.document.session.entity.ActiveFileSession;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentFileSessionRepository extends JpaRepository<ActiveFileSession, String> {
  @Query(
      value =
          "SELECT s.file_id FROM active_file_sessions s WHERE s.created_at < :cutoffDate ORDER BY s.created_at ASC LIMIT 50",
      nativeQuery = true)
  Set<String> findLastStaleRecords(@Param("cutoffDate") LocalDateTime cutoffDate);
}
