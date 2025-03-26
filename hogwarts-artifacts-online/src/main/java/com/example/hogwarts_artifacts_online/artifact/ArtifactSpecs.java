package com.example.hogwarts_artifacts_online.artifact;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ArtifactSpecs{


    public static Specification<Artifact> hasId(String providedId) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("id"), providedId);
    }

    public static Specification<Artifact> containsName(String providedName) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                "%" + providedName.toLowerCase() + "%");
    }

    public static Specification<Artifact> containsDescription(String providedDesc) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.
                lower(root.get("description")), "%" + providedDesc.toLowerCase() + "%");
    }

    public static Specification<Artifact> hasOwnerName(String providedOwnerName) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("owner").get("name")), providedOwnerName.toLowerCase());
    }





}
