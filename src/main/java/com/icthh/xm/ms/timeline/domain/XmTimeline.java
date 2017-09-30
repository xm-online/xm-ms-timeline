package com.icthh.xm.ms.timeline.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@ToString(exclude = {"httpStatusCode","startDate","requestBody","responseBody","requestHeaders","responseHeaders","browser","opSystem"})
public class XmTimeline {

    private String rid;
    private String login;
    private String userKey;
    private String tenant;
    private String msName;
    private String operationName;
    private Long entityId;
    private String entityKey;
    private String entityTypeKey;
    private String operationUrl;
    private String httpMethod;
    private Integer httpStatusCode;
    private Instant startDate;
    private String requestBody;
    private Long requestLength;
    private String responseBody;
    private Long responseLength;
    private String channelType;
    private Map<String, String> requestHeaders;
    private Map<String, String> responseHeaders;
    private Long execTime;
    private String browser;
    private String opSystem;

}
