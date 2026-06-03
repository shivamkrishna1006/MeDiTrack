package com.pm.dm.management.service;

import com.pm.dm.management.dto.medical.MedicalRecordRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class MedicalRecordServiceTests {

    private static final UUID SEEDED_PATIENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID SEEDED_DOCTOR_ID = UUID.fromString("423e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Test
    void shouldCreateMedicalRecord() {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO();
        request.setPatientId(SEEDED_PATIENT_ID);
        request.setDoctorId(SEEDED_DOCTOR_ID);
        request.setVisitDate(LocalDate.now());
        request.setDiagnosis("Seasonal allergy");
        request.setAllergies("Pollen");
        request.setNotes("Needs follow-up");

        var created = medicalRecordService.createMedicalRecord(request);

        assertThat(created.getId()).isNotBlank();
        assertThat(created.getDiagnosis()).isEqualTo("Seasonal allergy");
    }
}
