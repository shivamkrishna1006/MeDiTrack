package com.pm.dm.management.specification;

import com.pm.dm.management.model.Patient;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class PatientSpecification {
    private PatientSpecification() {
    }

    public static Specification<Patient> search(String search, String email, LocalDate registeredFrom, LocalDate registeredTo) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern)
                ));
            }
            if (email != null && !email.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (registeredFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("registrationDate"), registeredFrom));
            }
            if (registeredTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("registrationDate"), registeredTo));
            }
            return predicate;
        };
    }
}
