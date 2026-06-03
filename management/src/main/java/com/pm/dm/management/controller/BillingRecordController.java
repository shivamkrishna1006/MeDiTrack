package com.pm.dm.management.controller;

import com.pm.dm.management.dto.billing.BillingRecordRequestDTO;
import com.pm.dm.management.dto.billing.BillingRecordResponseDTO;
import com.pm.dm.management.dto.common.PageResponse;
import com.pm.dm.management.model.BillingStatus;
import com.pm.dm.management.service.BillingRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing-records")
@Tag(name = "Billing", description = "Manage billing records")
public class BillingRecordController {

    private final BillingRecordService billingRecordService;

    public BillingRecordController(BillingRecordService billingRecordService) {
        this.billingRecordService = billingRecordService;
    }

    @GetMapping
    @Operation(summary = "Get billing records with pagination and filters")
    public ResponseEntity<PageResponse<BillingRecordResponseDTO>> getBillingRecords(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) BillingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(billingRecordService.getBillingRecords(patientId, status, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get billing record by id")
    public ResponseEntity<BillingRecordResponseDTO> getBillingRecord(@PathVariable UUID id) {
        return ResponseEntity.ok(billingRecordService.getBillingRecord(id));
    }

    @PostMapping
    @Operation(summary = "Create a billing record")
    public ResponseEntity<BillingRecordResponseDTO> createBillingRecord(@Valid @RequestBody BillingRecordRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingRecordService.createBillingRecord(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a billing record")
    public ResponseEntity<BillingRecordResponseDTO> updateBillingRecord(@PathVariable UUID id, @Valid @RequestBody BillingRecordRequestDTO request) {
        return ResponseEntity.ok(billingRecordService.updateBillingRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a billing record")
    public ResponseEntity<Void> deleteBillingRecord(@PathVariable UUID id) {
        billingRecordService.deleteBillingRecord(id);
        return ResponseEntity.noContent().build();
    }
}
