package com.icthh.xm.ms.timeline.config;

/**
 * Application constants.
 */
public final class Constants {

    //Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String AUTH_TENANT_KEY = "tenant";
    public static final String AUTH_USER_KEY = "user_key";
    public static final String AUTH_XM_TOKEN_KEY = "xmToken";
    public static final String AUTH_XM_COOKIE_KEY = "xmCookie";
    public static final String AUTH_XM_USERID_KEY = "xmUserID";
    public static final String AUTH_XM_LOCALE = "xmLocale";
    public static final String HEADER_TENANT = "x-tenant";
    public static final String CASSANDRA_DROP_KEYSPACE = "DROP KEYSPACE IF EXISTS %s";
    public static final String CREATE_COMMAND = "CREATE";
    public static final String DELETE_COMMAND = "DELETE";

    public static final String CERTIFICATE = "X.509";
    public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----%n%s%n-----END PUBLIC KEY-----";

    //System event data fields
    public static final String EVENT_TENANT = "tenant";

    public static final String DEFAULT_CONFIG_PATH = "config/specs/default-timeline.yml";

    private Constants() {
    }
}