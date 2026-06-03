package com.pm.dm.management.specification;

import com.pm.dm.management.model.MedicalRecord;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public final class MedicalRecordSpecification {
    private MedicalRecordSpecification() {
    }

    public static Specification<MedicalRecord> search(UUID patientId, UUID doctorId, LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (patientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("patient").get("id"), patientId));
            }
            if (doctorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("doctor").get("id"), doctorId));
            }
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("visitDate"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("visitDate"), to));
            }
            return predicate;
        };
    }
}
