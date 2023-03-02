package com.icthh.xm.ms.timeline.service.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record TimelineEvent(Long id,
                            String rid,
                            String login,
                            String userKey,
                            String tenant,
                            String msName,
                            String operationName,
                            Long entityId,
                            String entityKey,
                            String entityTypeKey,
                            String operationUrl,
                            String operationQueryString,
                            String httpMethod,
                            Integer httpStatusCode,
                            Instant startDate,
                            String requestBody,
                            Long requestLength,
                            String responseBody,
                            Long responseLength,
                            String channelType,
                            Map<String, String> requestHeaders,
                            Map<String, String> responseHeaders,
                            Long execTime,
                            String browser,
                            String opSystem) {
}
