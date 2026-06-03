package com.pm.dm.management.service;

import com.pm.dm.management.dto.PatientRequestDTO;
import com.pm.dm.management.dto.PatientResponseDTO;
import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.exception.ConflictException;
import com.pm.dm.management.exception.ResourceNotFoundException;
import com.pm.dm.management.mapper.PatientMapper;
import com.pm.dm.management.model.Patient;
import com.pm.dm.management.repository.PatientRepository;
import com.pm.dm.management.specification.PatientSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService;

    public PatientService(PatientRepository patientRepository, AuditLogService auditLogService) {
        this.patientRepository = patientRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientResponseDTO> getPatients(String search,
                                                        String email,
                                                        LocalDate registeredFrom,
                                                        LocalDate registeredTo,
                                                        int page,
                                                        int size,
                                                        String sortBy,
                                                        String direction) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        var pageResult = patientRepository.findAll(PatientSpecification.search(search, email, registeredFrom, registeredTo), pageable)
                .map(PatientMapper::toDTO);
        return PageResponse.from(pageResult);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getPatient(UUID id) {
        return PatientMapper.toDTO(findPatient(id));
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        validateDates(patientRequestDTO);
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new ConflictException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }
        Patient saved = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        auditLogService.log("PATIENT_CREATED", "Patient", saved.getId(), saved.getEmail());
        return PatientMapper.toDTO(saved);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        validateDates(patientRequestDTO);
        Patient patient = findPatient(id);
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new ConflictException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }
        PatientMapper.updateModel(patient, patientRequestDTO);
        Patient updated = patientRepository.save(patient);
        auditLogService.log("PATIENT_UPDATED", "Patient", updated.getId(), updated.getEmail());
        return PatientMapper.toDTO(updated);
    }

    public void deletePatient(UUID id) {
        Patient patient = findPatient(id);
        patient.markDeleted();
        patientRepository.save(patient);
        auditLogService.log("PATIENT_DELETED", "Patient", id, patient.getEmail());
    }

    @Transactional(readOnly = true)
    public long countPatients() {
        return patientRepository.count();
    }

    @Transactional(readOnly = true)
    public long countNewPatientsThisMonth() {
        return patientRepository.countByCreatedAtGreaterThanEqual(java.time.LocalDate.now().withDayOfMonth(1).atStartOfDay());
    }

    public Patient findPatient(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    private void validateDates(PatientRequestDTO dto) {
        if (dto.getRegisteredDate().isBefore(dto.getDateOfBirth())) {
            throw new com.pm.dm.management.exception.BadRequestException("Registration date cannot be before date of birth");
        }
        if (dto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new com.pm.dm.management.exception.BadRequestException("Date of birth cannot be in the future");
        }
    }
}
