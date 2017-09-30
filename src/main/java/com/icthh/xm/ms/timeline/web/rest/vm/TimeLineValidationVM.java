package com.icthh.xm.ms.timeline.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class TimeLineValidationVM {

    private boolean isValid;
    private String errorMessage;

}
