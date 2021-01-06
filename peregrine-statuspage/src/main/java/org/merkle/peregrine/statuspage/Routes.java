package org.merkle.peregrine.statuspage;

public class Routes {

//    HTTP routes
    public static final String STATUS_PAGE_PUBLISH_V1 = "/v1/publishStatus";
    public static final String CREATE_SUBSCRIPTION_V1 = "/v1/addSubscription";
    public static final String PEREGRINE_STATUS_V1 = "/v1/peregrineStatus";

//    Vertx routes
    public static final String PUBLISH_STATUS_VERTX_V1 = "/v1/route/publishStatus";
    public static final String CREATE_SUBSCRIPTION_VERTX_V1 = "/v1/route/addSubscription";

    public static final String PEREGRINE_WEB_ROOT = "/";
}
