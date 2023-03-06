package com.icthh.xm.ms.timeline.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.icthh.xm.commons.migration.db.jsonb.Jsonb;
import com.icthh.xm.ms.timeline.domain.converter.MapToStringConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "xmtimeline")
@Getter
@Setter
@ToString(exclude = {"httpStatusCode", "startDate", "requestBody", "responseBody",
    "requestHeaders", "responseHeaders", "browser", "opSystem", "data", "entityBefore", "entityAfter"})
@NamedEntityGraph(name = "withHeaders",
                  attributeNodes = {
                      @NamedAttributeNode("requestHeaders"),
                      @NamedAttributeNode("responseHeaders")
                  })
public class XmTimeline implements Serializable {

    @Sorted
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Sorted
    @Column(name = "rid")
    private String rid;

    @Sorted
    @Column(name = "login")
    private String login;

    @Sorted
    @Column(name = "user_key")
    private String userKey;

    @Column(name = "tenant")
    private String tenant;

    @Sorted
    @Column(name = "ms_name")
    private String msName;

    @Sorted
    @Column(name = "operation_name")
    private String operationName;

    @Sorted
    @Column(name = "aggregate_id")
    private String aggregateId;

    @Sorted
    @Column(name = "entity_key")
    private String entityKey;

    @Sorted
    @Column(name = "aggregate_type")
    private String aggregateType;

    @Column(name = "operation_url")
    private String operationUrl;

    @Column(name = "operation_query_string")
    private String operationQueryString;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @Sorted
    @Column(name = "start_date")
    @Convert(converter = InstantConverter.class)
    private Instant startDate;

    @Lob
    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "request_length")
    private Long requestLength;

    @Lob
    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "response_length")
    private Long responseLength;

    @Sorted
    @Column(name = "client_id")
    private String clientId;

    @ElementCollection
    @MapKeyColumn(name = "header_key")
    @Column(name = "header_value")
    @CollectionTable(name = "timeline_request_headers", joinColumns = @JoinColumn(name = "timeline_id"))
    private Map<String, String> requestHeaders;

    @ElementCollection
    @MapKeyColumn(name = "header_key")
    @Column(name = "header_value")
    @CollectionTable(name = "timeline_response_headers", joinColumns = @JoinColumn(name = "timeline_id"))
    private Map<String, String> responseHeaders;

    @Column(name = "exec_time")
    private Long execTime;

    @Column(name = "browser")
    private String browser;

    @Column(name = "op_system")
    private String opSystem;

    @Column(name = "source")
    private String source;

    @Jsonb
    @JsonDeserialize(using = UntypedObjectDeserializer.class)
    @Convert(converter = MapToStringConverter.class)
    @Column(name = "data")
    private Map<String, Object> data = new HashMap<>();

    @Jsonb
    @JsonDeserialize(using = UntypedObjectDeserializer.class)
    @Convert(converter = MapToStringConverter.class)
    @Column(name = "entity_before")
    private Map<String, Object> entityBefore = new HashMap<>();

    @Jsonb
    @JsonDeserialize(using = UntypedObjectDeserializer.class)
    @Convert(converter = MapToStringConverter.class)
    @Column(name = "entity_after")
    private Map<String, Object> entityAfter = new HashMap<>();

}
