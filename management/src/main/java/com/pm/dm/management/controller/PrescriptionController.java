package com.pm.dm.management.controller;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.prescription.PrescriptionRequestDTO;
import com.pm.dm.management.dto.prescription.PrescriptionResponseDTO;
import com.pm.dm.management.model.PrescriptionStatus;
import com.pm.dm.management.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prescriptions")
@Tag(name = "Prescriptions", description = "Manage prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    @Operation(summary = "Get prescriptions with pagination and filters")
    public ResponseEntity<PageResponse<PrescriptionResponseDTO>> getPrescriptions(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) PrescriptionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(prescriptionService.getPrescriptions(patientId, doctorId, status, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by id")
    public ResponseEntity<PrescriptionResponseDTO> getPrescription(@PathVariable UUID id) {
        return ResponseEntity.ok(prescriptionService.getPrescription(id));
    }

    @PostMapping
    @Operation(summary = "Create a prescription")
    public ResponseEntity<PrescriptionResponseDTO> createPrescription(@Valid @RequestBody PrescriptionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.createPrescription(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a prescription")
    public ResponseEntity<PrescriptionResponseDTO> updatePrescription(@PathVariable UUID id, @Valid @RequestBody PrescriptionRequestDTO request) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a prescription")
    public ResponseEntity<Void> deletePrescription(@PathVariable UUID id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}
