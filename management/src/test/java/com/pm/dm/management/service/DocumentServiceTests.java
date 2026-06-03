package com.pm.dm.management.service;

import com.pm.dm.management.model.DocumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class DocumentServiceTests {

    private static final UUID SEEDED_PATIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private DocumentService documentService;

    @Test
    void shouldUploadDocumentMetadataAndDownloadContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.txt",
                "text/plain",
                "sample report".getBytes(StandardCharsets.UTF_8)
        );

        var uploaded = documentService.upload(SEEDED_PATIENT_ID, null, DocumentType.LAB_REPORT, file);
        var download = documentService.download(UUID.fromString(uploaded.getId()));

        assertThat(uploaded.getOriginalFilename()).isEqualTo("report.txt");
        assertThat(uploaded.getSizeBytes()).isEqualTo(13);
        assertThat(download.getResource().exists()).isTrue();
    }
}
