package com.icthh.xm.ms.timeline.service.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record TimelineDto(Long id,
                          String txId,
                          String rid,
                          String login,
                          String userKey,
                          String tenant,
                          String msName,
                          String operationName,
                          String aggregateId,
                          String aggregateName,
                          String entityKey,
                          String aggregateType,
                          String operationUrl,
                          String operationQueryString,
                          String httpMethod,
                          Integer httpStatusCode,
                          Instant startDate,
                          Instant validFrom,
                          Instant validTo,
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
                          Map<String, Object> meta,
                          Map<String, Object> data,
                          Map<String, Object> entityBefore,
                          Map<String, Object> entityAfter) {
}
