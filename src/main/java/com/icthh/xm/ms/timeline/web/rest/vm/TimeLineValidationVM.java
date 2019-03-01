package com.icthh.xm.ms.timeline.web.rest.vm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeLineValidationVM {

    private boolean isValid;
    private String errorMessage;

}
