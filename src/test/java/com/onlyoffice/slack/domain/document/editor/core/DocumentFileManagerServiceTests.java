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
package com.onlyoffice.slack.domain.document.editor.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hazelcast.map.IMap;
import com.onlyoffice.model.common.Format;
import com.onlyoffice.model.documenteditor.config.document.DocumentType;
import com.onlyoffice.slack.domain.document.DocumentServerFormatsConfiguration;
import com.onlyoffice.slack.shared.configuration.ServerConfigurationProperties;
import com.onlyoffice.slack.shared.transfer.cache.DocumentSessionKey;
import com.slack.api.model.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentFileManagerServiceTests {
  @Mock ServerConfigurationProperties serverConfigurationProperties;
  @Mock DocumentServerFormatsConfiguration formatsConfiguration;

  @Mock DocumentFileKeyBuilder documentFileKeyBuilder;

  @Mock IMap<String, DocumentSessionKey> keys;
  @Mock DocumentJwtManagerService documentJwtManagerService;
  @InjectMocks DocumentFileManagerServiceImpl documentFileManagerService;

  @Test
  void whenGetExtensionWithValidFile_thenReturnExtension() {
    var file = new File();
    file.setName("test.docx");

    assertEquals("docx", documentFileManagerService.getExtension(file));
  }

  @Test
  void whenGetExtensionWithNoExtension_thenReturnNull() {
    var file = new File();
    file.setName("testfile");

    assertNull(documentFileManagerService.getExtension(file));
  }

  @Test
  void whenGetDocumentTypeWithKnownExtension_thenReturnType() {
    var file = new File();
    file.setName("test.docx");

    var format = mock(Format.class);

    when(format.getName()).thenReturn("docx");
    when(format.getType()).thenReturn(DocumentType.WORD);
    when(formatsConfiguration.getFormats()).thenReturn(List.of(format));
    assertEquals(DocumentType.WORD, documentFileManagerService.getDocumentType(file));
  }

  @Test
  void whenIsEditableWithEditableFile_thenReturnTrue() {
    var file = new File();
    file.setName("test.docx");
    var format = mock(Format.class);

    when(format.getName()).thenReturn("docx");
    when(format.getActions()).thenReturn(List.of("edit"));
    when(formatsConfiguration.getFormats()).thenReturn(List.of(format));
    assertTrue(documentFileManagerService.isEditable(file));
  }
}
