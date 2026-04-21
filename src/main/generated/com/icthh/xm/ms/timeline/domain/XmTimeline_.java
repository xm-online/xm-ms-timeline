package com.icthh.xm.ms.timeline.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import java.util.Map;

@StaticMetamodel(XmTimeline.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class XmTimeline_ {

	public static final String ENTITY_AFTER = "entityAfter";
	public static final String RESPONSE_BODY = "responseBody";
	public static final String DATA = "data";
	public static final String RESPONSE_LENGTH = "responseLength";
	public static final String EXEC_TIME = "execTime";
	public static final String REQUEST_LENGTH = "requestLength";
	public static final String SOURCE = "source";
	public static final String RID = "rid";
	public static final String LOGIN = "login";
	public static final String HTTP_METHOD = "httpMethod";
	public static final String AGGREGATE_TYPE = "aggregateType";
	public static final String AGGREGATE_ID = "aggregateId";
	public static final String REQUEST_BODY = "requestBody";
	public static final String BROWSER = "browser";
	public static final String ID = "id";
	public static final String TENANT = "tenant";
	public static final String OPERATION_URL = "operationUrl";
	public static final String CLIENT_ID = "clientId";
	public static final String OPERATION_QUERY_STRING = "operationQueryString";
	public static final String GRAPH_WITH_HEADERS = "withHeaders";
	public static final String ENTITY_KEY = "entityKey";
	public static final String MS_NAME = "msName";
	public static final String OPERATION_NAME = "operationName";
	public static final String OP_SYSTEM = "opSystem";
	public static final String USER_KEY = "userKey";
	public static final String ENTITY_BEFORE = "entityBefore";
	public static final String REQUEST_HEADERS = "requestHeaders";
	public static final String RESPONSE_HEADERS = "responseHeaders";
	public static final String START_DATE = "startDate";
	public static final String HTTP_STATUS_CODE = "httpStatusCode";

	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#entityAfter
	 **/
	public static volatile SingularAttribute<XmTimeline, Map<String,Object>> entityAfter;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#responseBody
	 **/
	public static volatile SingularAttribute<XmTimeline, String> responseBody;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#data
	 **/
	public static volatile SingularAttribute<XmTimeline, Map<String,Object>> data;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#responseLength
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> responseLength;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#execTime
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> execTime;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#requestLength
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> requestLength;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#source
	 **/
	public static volatile SingularAttribute<XmTimeline, String> source;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#rid
	 **/
	public static volatile SingularAttribute<XmTimeline, String> rid;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#login
	 **/
	public static volatile SingularAttribute<XmTimeline, String> login;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#httpMethod
	 **/
	public static volatile SingularAttribute<XmTimeline, String> httpMethod;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#aggregateType
	 **/
	public static volatile SingularAttribute<XmTimeline, String> aggregateType;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#aggregateId
	 **/
	public static volatile SingularAttribute<XmTimeline, String> aggregateId;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#requestBody
	 **/
	public static volatile SingularAttribute<XmTimeline, String> requestBody;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#browser
	 **/
	public static volatile SingularAttribute<XmTimeline, String> browser;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#id
	 **/
	public static volatile SingularAttribute<XmTimeline, Long> id;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline
	 **/
	public static volatile EntityType<XmTimeline> class_;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#tenant
	 **/
	public static volatile SingularAttribute<XmTimeline, String> tenant;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#operationUrl
	 **/
	public static volatile SingularAttribute<XmTimeline, String> operationUrl;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#clientId
	 **/
	public static volatile SingularAttribute<XmTimeline, String> clientId;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#operationQueryString
	 **/
	public static volatile SingularAttribute<XmTimeline, String> operationQueryString;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#entityKey
	 **/
	public static volatile SingularAttribute<XmTimeline, String> entityKey;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#msName
	 **/
	public static volatile SingularAttribute<XmTimeline, String> msName;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#operationName
	 **/
	public static volatile SingularAttribute<XmTimeline, String> operationName;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#opSystem
	 **/
	public static volatile SingularAttribute<XmTimeline, String> opSystem;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#userKey
	 **/
	public static volatile SingularAttribute<XmTimeline, String> userKey;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#entityBefore
	 **/
	public static volatile SingularAttribute<XmTimeline, Map<String,Object>> entityBefore;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#requestHeaders
	 **/
	public static volatile MapAttribute<XmTimeline, String, String> requestHeaders;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#responseHeaders
	 **/
	public static volatile MapAttribute<XmTimeline, String, String> responseHeaders;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#startDate
	 **/
	public static volatile SingularAttribute<XmTimeline, Instant> startDate;
	
	/**
	 * @see com.icthh.xm.ms.timeline.domain.XmTimeline#httpStatusCode
	 **/
	public static volatile SingularAttribute<XmTimeline, Integer> httpStatusCode;

}

