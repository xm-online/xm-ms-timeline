package com.icthh.xm.ms.timeline.web.rest.vm;

import com.icthh.xm.ms.timeline.domain.XmTimeline;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimelinePageVM {

    private List<XmTimeline> timelines;

    private String next;

    public TimelinePageVM(List<XmTimeline> timelines, String next) {
        this.timelines = timelines;
        this.next = next;
    }

    @Override
    public String toString() {
        return "TimelinePageVM{"
            + "timelines.size=" + (timelines == null ? 0 : timelines.size())
            + ", next='" + next + '\''
            + '}';
    }
}
