package com.icthh.xm.ms.timeline.repository.jpa;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TimelineJpaRepository extends PagingAndSortingRepository<XmTimeline, Long>, JpaSpecificationExecutor {

    @EntityGraph(value = "withHeaders", type = EntityGraph.EntityGraphType.LOAD)
    Page<XmTimeline> findAll(Specification spec, Pageable pageable);

    @EntityGraph(value = "withHeaders", type = EntityGraph.EntityGraphType.LOAD)
    Page<XmTimeline> findAll(Pageable pageable);
}
