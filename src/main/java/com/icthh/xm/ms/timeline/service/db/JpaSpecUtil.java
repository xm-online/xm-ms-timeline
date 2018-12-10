package com.icthh.xm.ms.timeline.service.db;

import java.time.Instant;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

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

    public <T> Specifications<T> combineEqualSpecifications(Specifications<T> prevSpec,
                                                            String filterValue,
                                                            String propertyName) {
        Specifications<T> specifications = Specifications.where(equalSpecification(filterValue, propertyName));
        return prevSpec != null ? prevSpec.and(specifications) : specifications;
    }

    public <T> Specifications<T> combineLessThanOrEqualToSpecifications(Specifications<T> prevSpec,
                                                                        Instant filterValue,
                                                                        String propertyName) {
        Specifications<T> specifications = Specifications.where(
            lessThanOrEqualToSpecification(filterValue, propertyName));

        return prevSpec != null ? prevSpec.and(specifications) : specifications;
    }

    public <T> Specifications<T> combineGreaterThanOrEqualToSpecifications(Specifications<T> prevSpec,
                                                                           Instant filterValue,
                                                                           String propertyName) {
        Specifications<T> specifications = Specifications.where(
            greaterThanOrEqualToSpecification(filterValue, propertyName));

        return prevSpec != null ? prevSpec.and(specifications) : specifications;
    }
}
