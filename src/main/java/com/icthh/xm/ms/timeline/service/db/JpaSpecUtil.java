package com.icthh.xm.ms.timeline.service.db;

import java.time.Instant;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class JpaSpecUtil {

    public static <T> Specification<T> equalSpecification(String filterValue,
                                                          String propertyName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder)
            -> builder.equal(root.get(propertyName), filterValue);
    }

    public static <T> Specification<T> lessThanOrEqualToSpecification(Instant filterValue,
                                                                      String propertyName) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get(propertyName), filterValue);
    }

    public static <T> Specification<T> greaterThanOrEqualToSpecification(Instant filterValue,
                                                                         String propertyName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder)
            -> builder.greaterThanOrEqualTo(root.get(propertyName), filterValue);
    }

    public static <T> Specification<T> combineEqualSpecifications(Specification<T> prevSpec,
                                                                  String filterValue,
                                                                  String propertyName) {
        Specification<T> specification = Specification.where(equalSpecification(filterValue, propertyName));
        return prevSpec != null ? prevSpec.and(specification) : specification;
    }

    public static <T> Specification<T> combineLessThanOrEqualToSpecifications(Specification<T> prevSpec,
                                                                              Instant filterValue,
                                                                              String propertyName) {
        Specification<T> specification = Specification.where(
            lessThanOrEqualToSpecification(filterValue, propertyName));

        return prevSpec != null ? prevSpec.and(specification) : specification;
    }

    public static <T> Specification<T> combineGreaterThanOrEqualToSpecifications(Specification<T> prevSpec,
                                                                                 Instant filterValue,
                                                                                 String propertyName) {
        Specification<T> specification = Specification.where(
            greaterThanOrEqualToSpecification(filterValue, propertyName));

        return prevSpec != null ? prevSpec.and(specification) : specification;
    }
}
