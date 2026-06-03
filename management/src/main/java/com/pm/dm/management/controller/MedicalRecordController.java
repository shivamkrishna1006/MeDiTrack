package com.pm.dm.management.controller;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.medical.MedicalRecordRequestDTO;
import com.pm.dm.management.dto.medical.MedicalRecordResponseDTO;
import com.pm.dm.management.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/medical-records")
@Tag(name = "Medical Records", description = "Manage patient medical records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    @Operation(summary = "Get medical records with pagination and filters")
    public ResponseEntity<PageResponse<MedicalRecordResponseDTO>> getMedicalRecords(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "visitDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecords(patientId, doctorId, from, to, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medical record by id")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecord(@PathVariable UUID id) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecord(id));
    }

    @PostMapping
    @Operation(summary = "Create a medical record")
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(@Valid @RequestBody MedicalRecordRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRecordService.createMedicalRecord(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a medical record")
    public ResponseEntity<MedicalRecordResponseDTO> updateMedicalRecord(@PathVariable UUID id, @Valid @RequestBody MedicalRecordRequestDTO request) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a medical record")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable UUID id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}
