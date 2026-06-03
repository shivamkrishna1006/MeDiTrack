package com.pm.dm.management.repository;

import com.pm.dm.management.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID>, JpaSpecificationExecutor<Prescription> {
}
