package com.icthh.xm.ms.timeline.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import java.util.Map;

/**
 * Static metamodel for {@link com.icthh.xm.ms.timeline.domain.XmTimeline}
 **/
@StaticMetamodel(XmTimeline.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class XmTimeline_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #rid
	 **/
	public static final String RID = "rid";
	
	/**
	 * @see #login
	 **/
	public static final String LOGIN = "login";
	
	/**
	 * @see #userKey
	 **/
	public static final String USER_KEY = "userKey";
	
	/**
	 * @see #tenant
	 **/
	public static final String TENANT = "tenant";
	
	/**
	 * @see #msName
	 **/
	public static final String MS_NAME = "msName";
	
	/**
	 * @see #operationName
	 **/
	public static final String OPERATION_NAME = "operationName";
	
	/**
	 * @see #aggregateId
	 **/
	public static final String AGGREGATE_ID = "aggregateId";
	
	/**
	 * @see #entityKey
	 **/
	public static final String ENTITY_KEY = "entityKey";
	
	/**
	 * @see #aggregateType
	 **/
	public static final String AGGREGATE_TYPE = "aggregateType";
	
	/**
	 * @see #operationUrl
	 **/
	public static final String OPERATION_URL = "operationUrl";
	
	/**
	 * @see #operationQueryString
	 **/
	public static final String OPERATION_QUERY_STRING = "operationQueryString";
	
	/**
	 * @see #httpMethod
	 **/
	public static final String HTTP_METHOD = "httpMethod";
	
	/**
	 * @see #httpStatusCode
	 **/
	public static final String HTTP_STATUS_CODE = "httpStatusCode";
	
	/**
	 * @see #startDate
	 **/
	public static final String START_DATE = "startDate";
	
	/**
	 * @see #requestBody
	 **/
	public static final String REQUEST_BODY = "requestBody";
	
	/**
	 * @see #requestLength
	 **/
	public static final String REQUEST_LENGTH = "requestLength";
	
	/**
	 * @see #responseBody
	 **/
	public static final String RESPONSE_BODY = "responseBody";
	
	/**
	 * @see #responseLength
	 **/
	public static final String RESPONSE_LENGTH = "responseLength";
	
	/**
	 * @see #clientId
	 **/
	public static final String CLIENT_ID = "clientId";
	
	/**
	 * @see #requestHeaders
	 **/
	public static final String REQUEST_HEADERS = "requestHeaders";
	
	/**
	 * @see #responseHeaders
	 **/
	public static final String RESPONSE_HEADERS = "responseHeaders";
	
	/**
	 * @see #execTime
	 **/
	public static final String EXEC_TIME = "execTime";
	
	/**
	 * @see #browser
	 **/
	public static final String BROWSER = "browser";
	
	/**
	 * @see #opSystem
	 **/
	public static final String OP_SYSTEM = "opSystem";
	
	/**
	 * @see #source
	 **/
	public static final String SOURCE = "source";
	
	/**
	 * @see #data
	 **/
	public static final String DATA = "data";
	
	/**
	 * @see #entityBefore
	 **/
	public static final String ENTITY_BEFORE = "entityBefore";
	
	/**
	 * @see #entityAfter
	 **/
	public static final String ENTITY_AFTER = "entityAfter";
	
	/**
	 * @see #_withHeaders
	 **/
	public static final String GRAPH_WITH_HEADERS = "withHeaders";

	
	/**
	 * Static metamodel type for {@link com.icthh.xm.ms.timeline.domain.XmTimeline}
	 **/
	public static volatile EntityType<XmTimeline> class_;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#id}
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> id;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#rid}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> rid;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#login}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> login;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#userKey}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> userKey;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#tenant}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> tenant;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#msName}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> msName;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#operationName}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> operationName;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#aggregateId}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> aggregateId;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#entityKey}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> entityKey;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#aggregateType}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> aggregateType;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#operationUrl}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> operationUrl;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#operationQueryString}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> operationQueryString;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#httpMethod}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> httpMethod;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#httpStatusCode}
	 **/
	public static volatile SingularAttribute<XmTimeline, Integer> httpStatusCode;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#startDate}
	 **/
	public static volatile SingularAttribute<XmTimeline, Instant> startDate;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#requestBody}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> requestBody;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#requestLength}
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> requestLength;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#responseBody}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> responseBody;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#responseLength}
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> responseLength;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#clientId}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> clientId;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#requestHeaders}
	 **/
	public static volatile MapAttribute<XmTimeline, String, String> requestHeaders;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#responseHeaders}
	 **/
	public static volatile MapAttribute<XmTimeline, String, String> responseHeaders;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#execTime}
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> execTime;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#browser}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> browser;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#opSystem}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> opSystem;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#source}
	 **/
	public static volatile SingularAttribute<XmTimeline, String> source;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#data}
	 **/
	public static volatile SingularAttribute<XmTimeline, Map> data;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#entityBefore}
	 **/
	public static volatile SingularAttribute<XmTimeline, Map> entityBefore;
	
	/**
	 * Static metamodel for attribute {@link com.icthh.xm.ms.timeline.domain.XmTimeline#entityAfter}
	 **/
	public static volatile SingularAttribute<XmTimeline, Map> entityAfter;
	
	/**
	 * The entity graph named {@value GRAPH_WITH_HEADERS}
	 *
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline
	 **/
	public static volatile EntityGraph<XmTimeline> _withHeaders;

}

