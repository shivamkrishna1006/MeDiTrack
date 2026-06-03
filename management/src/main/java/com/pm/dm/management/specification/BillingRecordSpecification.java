package com.pm.dm.management.specification;

import com.pm.dm.management.model.BillingRecord;
import com.pm.dm.management.model.BillingStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class BillingRecordSpecification {
    private BillingRecordSpecification() {
    }

    public static Specification<BillingRecord> search(UUID patientId, BillingStatus status) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (patientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("patient").get("id"), patientId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            return predicate;
        };
    }
}
