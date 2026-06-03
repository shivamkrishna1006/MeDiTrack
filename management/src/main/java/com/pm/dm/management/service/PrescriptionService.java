package com.pm.dm.management.service;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.prescription.PrescriptionRequestDTO;
import com.pm.dm.management.dto.prescription.PrescriptionResponseDTO;
import com.pm.dm.management.exception.BadRequestException;
import com.pm.dm.management.exception.ResourceNotFoundException;
import com.pm.dm.management.mapper.PrescriptionMapper;
import com.pm.dm.management.model.Prescription;
import com.pm.dm.management.model.PrescriptionStatus;
import com.pm.dm.management.repository.PrescriptionRepository;
import com.pm.dm.management.specification.PrescriptionSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final MedicalRecordService medicalRecordService;
    private final AuditLogService auditLogService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PatientService patientService,
                               DoctorService doctorService,
                               MedicalRecordService medicalRecordService,
                               AuditLogService auditLogService) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.medicalRecordService = medicalRecordService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public PageResponse<PrescriptionResponseDTO> getPrescriptions(UUID patientId,
                                                                  UUID doctorId,
                                                                  PrescriptionStatus status,
                                                                  int page,
                                                                  int size,
                                                                  String sortBy,
                                                                  String direction) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        var pageResult = prescriptionRepository.findAll(PrescriptionSpecification.search(patientId, doctorId, status), pageable)
                .map(PrescriptionMapper::toDTO);
        return PageResponse.from(pageResult);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponseDTO getPrescription(UUID id) {
        return PrescriptionMapper.toDTO(findPrescription(id));
    }

    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO request) {
        Prescription prescription = new Prescription();
        apply(prescription, request);
        Prescription saved = prescriptionRepository.save(prescription);
        auditLogService.log("PRESCRIPTION_CREATED", "Prescription", saved.getId(), saved.getMedicationName());
        return PrescriptionMapper.toDTO(saved);
    }

    public PrescriptionResponseDTO updatePrescription(UUID id, PrescriptionRequestDTO request) {
        Prescription prescription = findPrescription(id);
        apply(prescription, request);
        Prescription updated = prescriptionRepository.save(prescription);
        auditLogService.log("PRESCRIPTION_UPDATED", "Prescription", updated.getId(), updated.getMedicationName());
        return PrescriptionMapper.toDTO(updated);
    }

    public void deletePrescription(UUID id) {
        Prescription prescription = findPrescription(id);
        prescription.markDeleted();
        prescriptionRepository.save(prescription);
    }

    private Prescription findPrescription(UUID id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));
    }

        @Transactional(readOnly = true)
    public long countPrescriptions() {
        return prescriptionRepository.count();
    }

    private void apply(Prescription prescription, PrescriptionRequestDTO request) {
        var patient = patientService.findPatient(request.getPatientId());
        var doctor = doctorService.findDoctor(request.getDoctorId());
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setMedicationName(request.getMedicationName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setDurationInDays(request.getDurationInDays());
        prescription.setInstructions(request.getInstructions());
        prescription.setStatus(request.getStatus());

        if (request.getMedicalRecordId() != null) {
            var medicalRecord = medicalRecordService.findMedicalRecord(request.getMedicalRecordId());
            if (!medicalRecord.getPatient().getId().equals(patient.getId())) {
                throw new BadRequestException("Medical record patient does not match prescription patient");
            }
            prescription.setMedicalRecord(medicalRecord);
        } else {
            prescription.setMedicalRecord(null);
        }
    }
}
