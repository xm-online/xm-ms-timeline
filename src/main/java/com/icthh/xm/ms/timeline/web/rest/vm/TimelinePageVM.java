package com.icthh.xm.ms.timeline.web.rest.vm;

import com.icthh.xm.ms.timeline.domain.XmTimeline;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TimelinePageVM {

    private List<XmTimeline> timelines;

    private String next;

    public TimelinePageVM(List<XmTimeline> timelines, String next) {
        this.timelines = timelines;
        this.next = next;
    }
}
