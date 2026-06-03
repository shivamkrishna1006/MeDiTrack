package com.pm.dm.management.service;

import com.pm.dm.management.dto.PatientRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class PatientServiceTests {

    @Autowired
    private PatientService patientService;

    @Test
    void shouldCreateAndFilterPatients() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setFirstName("Shivam");
        request.setLastName("Krishna");
        request.setEmail("shivam.krishna@example.com");
        request.setAddress("Bangalore");
        request.setDateOfBirth(LocalDate.of(2002, 5, 19));
        request.setRegisteredDate(LocalDate.of(2026, 1, 10));

        var created = patientService.createPatient(request);
        assertThat(created.getId()).isNotBlank();

        var result = patientService.getPatients("shivam", null, null, null, 0, 10, "createdAt", "desc");
        assertThat(result.getContent()).extracting("email").contains("shivam.krishna@example.com");
    }
}
