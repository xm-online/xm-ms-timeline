package com.icthh.xm.ms.timeline.web.rest.vm;

import java.util.List;

import com.icthh.xm.ms.timeline.service.dto.TimelineEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimelinePageVM {

    private List<TimelineEvent> timelines;

    private String next;

    public TimelinePageVM(List<TimelineEvent> timelines, String next) {
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
