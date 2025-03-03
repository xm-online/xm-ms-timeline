package com.icthh.xm.ms.timeline.repository.jpa;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineJpaRepository extends JpaRepository<XmTimeline, Long>, JpaSpecificationExecutor {

    Page<XmTimeline> findAll(Specification spec, Pageable pageable);

    Page<XmTimeline> findAll(Pageable pageable);

    /**
     * Used 2 sql queries to avoid pagination in memory:
     * HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
     * more details: https://vladmihalcea.com/fix-hibernate-hhh000104-entity-fetch-pagination-warning-message/
     */
    default Page<XmTimeline> findAllWithHeaders(Specification spec, Pageable pageable) {
        Page<XmTimeline> timelines = findAll(spec, pageable);
        List<XmTimeline> timeLinesWithHeaders = findByIdIn(timelines.map(XmTimeline::getId).getContent(), pageable.getSort());
        return new PageImpl<>(timeLinesWithHeaders, pageable, timelines.getTotalElements());
    }

    default Page<XmTimeline> findAllWithHeaders(Pageable pageable) {
        Page<XmTimeline> timelines = findAll(pageable);
        List<XmTimeline> timeLinesWithHeaders = findByIdIn(timelines.map(XmTimeline::getId).getContent(), pageable.getSort());
        return new PageImpl<>(timeLinesWithHeaders, pageable, timelines.getTotalElements());
    }

    @EntityGraph(value = "withHeaders", type = EntityGraph.EntityGraphType.LOAD)
    List<XmTimeline> findByIdIn(List<Long> userIds, Sort sort);

}
