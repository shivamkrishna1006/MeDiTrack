package com.pm.dm.management.specification;

import com.pm.dm.management.model.Document;
import com.pm.dm.management.model.DocumentType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class DocumentSpecification {
    private DocumentSpecification() {
    }

    public static Specification<Document> search(UUID patientId, DocumentType type) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (patientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("patient").get("id"), patientId));
            }
            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("documentType"), type));
            }
            return predicate;
        };
    }
}
