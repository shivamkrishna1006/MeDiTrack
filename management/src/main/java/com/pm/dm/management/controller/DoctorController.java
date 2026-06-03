package com.pm.dm.management.controller;

import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.dto.doctor.DoctorRequestDTO;
import com.pm.dm.management.dto.doctor.DoctorResponseDTO;
import com.pm.dm.management.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/doctors")
@Tag(name = "Doctors", description = "Manage doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    @Operation(summary = "Get all doctors with pagination and filtering")
    public ResponseEntity<PageResponse<DoctorResponseDTO>> getDoctors(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(doctorService.getDoctors(search, specialization, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by id")
    public ResponseEntity<DoctorResponseDTO> getDoctor(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctor(id));
    }

    @PostMapping
    @Operation(summary = "Create a doctor")
    public ResponseEntity<DoctorResponseDTO> createDoctor(@Valid @RequestBody DoctorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a doctor")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable UUID id, @Valid @RequestBody DoctorRequestDTO request) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a doctor")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
