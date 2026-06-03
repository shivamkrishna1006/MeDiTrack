package com.pm.dm.management.specification;

import com.pm.dm.management.model.AuditLog;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class AuditLogSpecification {
    private AuditLogSpecification() {
    }

    public static Specification<AuditLog> search(String actor, String action, String entityType, UUID entityId) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (actor != null && !actor.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("actor")), "%" + actor.toLowerCase() + "%"));
            }
            if (action != null && !action.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("action"), action));
            }
            if (entityType != null && !entityType.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("entityType"), entityType));
            }
            if (entityId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("entityId"), entityId));
            }
            return predicate;
        };
    }
}
