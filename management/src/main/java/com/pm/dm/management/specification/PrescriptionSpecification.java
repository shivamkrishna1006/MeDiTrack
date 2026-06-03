package com.pm.dm.management.specification;

import com.pm.dm.management.model.Prescription;
import com.pm.dm.management.model.PrescriptionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class PrescriptionSpecification {
    private PrescriptionSpecification() {
    }

    public static Specification<Prescription> search(UUID patientId, UUID doctorId, PrescriptionStatus status) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (patientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("patient").get("id"), patientId));
            }
            if (doctorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("doctor").get("id"), doctorId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            return predicate;
        };
    }
}
