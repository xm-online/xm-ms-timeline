package com.icthh.xm.ms.timeline.repository.jpa;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimelineJpaRepository extends JpaRepository<XmTimeline, Long>, JpaSpecificationExecutor {

}
