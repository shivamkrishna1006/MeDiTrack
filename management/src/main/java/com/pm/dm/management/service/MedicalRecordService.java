package com.pm.dm.management.service;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.medical.MedicalRecordRequestDTO;
import com.pm.dm.management.dto.medical.MedicalRecordResponseDTO;
import com.pm.dm.management.exception.ResourceNotFoundException;
import com.pm.dm.management.mapper.MedicalRecordMapper;
import com.pm.dm.management.model.MedicalRecord;
import com.pm.dm.management.repository.MedicalRecordRepository;
import com.pm.dm.management.specification.MedicalRecordSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                PatientService patientService,
                                DoctorService doctorService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @Transactional(readOnly = true)
    public PageResponse<MedicalRecordResponseDTO> getMedicalRecords(UUID patientId,
                                                                    UUID doctorId,
                                                                    LocalDate from,
                                                                    LocalDate to,
                                                                    int page,
                                                                    int size,
                                                                    String sortBy,
                                                                    String direction) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        var pageResult = medicalRecordRepository.findAll(MedicalRecordSpecification.search(patientId, doctorId, from, to), pageable)
                .map(MedicalRecordMapper::toDTO);
        return PageResponse.from(pageResult);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getMedicalRecord(UUID id) {
        return MedicalRecordMapper.toDTO(findMedicalRecord(id));
    }

    public MedicalRecordResponseDTO createMedicalRecord(MedicalRecordRequestDTO request) {
        MedicalRecord record = new MedicalRecord();
        apply(record, request);
        return MedicalRecordMapper.toDTO(medicalRecordRepository.save(record));
    }

    public MedicalRecordResponseDTO updateMedicalRecord(UUID id, MedicalRecordRequestDTO request) {
        MedicalRecord record = findMedicalRecord(id);
        apply(record, request);
        return MedicalRecordMapper.toDTO(medicalRecordRepository.save(record));
    }

    public void deleteMedicalRecord(UUID id) {
        MedicalRecord record = findMedicalRecord(id);
        record.markDeleted();
        medicalRecordRepository.save(record);
    }

    public MedicalRecord findMedicalRecord(UUID id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + id));
    }

        @Transactional(readOnly = true)
    public long countMedicalRecords() {
        return medicalRecordRepository.count();
    }

    private void apply(MedicalRecord record, MedicalRecordRequestDTO request) {
        record.setPatient(patientService.findPatient(request.getPatientId()));
        record.setDoctor(doctorService.findDoctor(request.getDoctorId()));
        record.setVisitDate(request.getVisitDate());
        record.setDiagnosis(request.getDiagnosis());
        record.setAllergies(request.getAllergies());
        record.setNotes(request.getNotes());
    }
}
