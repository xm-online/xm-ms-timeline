package com.icthh.xm.ms.timeline.service;

import com.icthh.xm.ms.timeline.domain.Sorted;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class SortProcessor {

    public Sort findValidOrDefault(Class<?> entityClass, Sort sort, Sort defaultSort) {
        List<String> availableFields = findSortedFields(entityClass);

        List<Sort.Order> orders = sort.stream()
            .filter(order -> availableFields.contains(order.getProperty()))
            .collect(toList());

        if (orders.isEmpty()) {
            return defaultSort;
        }

        return Sort.by(orders);
    }

    private List<String> findSortedFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Sorted.class))
            .map(Field::getName)
            .collect(toList());
    }
}
