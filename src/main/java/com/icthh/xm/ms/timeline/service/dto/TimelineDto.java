package com.icthh.xm.ms.timeline.service.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record TimelineDto(Long id,
                          String rid,
                          String login,
                          String userKey,
                          String tenant,
                          String msName,
                          String operationName,
                          String aggregateId,
                          String entityKey,
                          String aggregateType,
                          String operationUrl,
                          String operationQueryString,
                          String httpMethod,
                          Integer httpStatusCode,
                          Instant startDate,
                          String requestBody,
                          Long requestLength,
                          String responseBody,
                          Long responseLength,
                          String clientId,
                          Map<String, String> requestHeaders,
                          Map<String, String> responseHeaders,
                          Long execTime,
                          String browser,
                          String opSystem,
                          String source,
                          Map<String, Object> data,
                          Map<String, Object> entityBefore,
                          Map<String, Object> entityAfter) {
}
