package com.jcmlabs.AccessCore.Utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BaseFilterUtilities {
    public static <T, F extends BaseFilterDto> Specification<T> filterByCriteria(F filterInput, Class<T> entityClass) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (filterInput.getKeyword() != null && !filterInput.getKeyword().isEmpty()) {
                List<Predicate> searchPredicates = new ArrayList<>();
                String searchValue = "%" + filterInput.getKeyword().toLowerCase() + "%";

                for (Field field : entityClass.getDeclaredFields()) {
                    if (field.getType().equals(String.class)) {
                        searchPredicates.add(cb.like(cb.lower(root.get(field.getName())), searchValue));
                    }
                }

                if (!searchPredicates.isEmpty()) {
                    predicate = cb.and(predicate, cb.or(searchPredicates.toArray(new Predicate[0])));
                }
            }

            if (filterInput.getUuid() != null && !filterInput.getUuid().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("uuid"), filterInput.getUuid()));
            }

            if (filterInput.getDateFrom() != null && filterInput.getDateTo() != null) {
                predicate = cb.and(predicate, cb.between(root.get("createdAt"), filterInput.getDateFrom(), filterInput.getDateTo()));
            }

            if (filterInput.getCreatedBy() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("createdBy"), filterInput.getCreatedBy()));
            }

            if (filterInput.getUpdatedBy() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("updatedBy"), filterInput.getUpdatedBy()));
            }

            if (filterInput.getActive() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("active"), filterInput.getActive()));
            }
            return predicate;
        };
    }
}
