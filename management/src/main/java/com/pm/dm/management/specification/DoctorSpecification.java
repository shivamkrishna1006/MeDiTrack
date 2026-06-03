package com.pm.dm.management.specification;

import com.pm.dm.management.model.Doctor;
import org.springframework.data.jpa.domain.Specification;

public final class DoctorSpecification {
    private DoctorSpecification() {
    }

    public static Specification<Doctor> search(String search, String specialization) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                ));
            }
            if (specialization != null && !specialization.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("specialization")), "%" + specialization.toLowerCase() + "%"));
            }
            return predicate;
        };
    }
}
