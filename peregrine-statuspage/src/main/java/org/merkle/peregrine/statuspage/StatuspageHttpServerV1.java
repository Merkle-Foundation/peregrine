package org.merkle.peregrine.statuspage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;

public class StatuspageHttpServerV1 extends AbstractVerticle {

    private static final String PUBLISH_STATUS_V1 = "/v1/publishStatus";
    private static final String PEREGRINE_STATUS_V1 = "/v1/peregrineStatus";

    @Override
    public void start() throws Exception {
        final HttpServer httpServer = vertx.createHttpServer();

        final Router router = Router.router(vertx);
        router.route(HttpMethod.POST, PUBLISH_STATUS_V1).handler(this::statusPubHandler);
        router.route(HttpMethod.GET, PEREGRINE_STATUS_V1).handler(this::peregrineUp);

        httpServer
                .requestHandler(router)
                .listen(8122);
    }

    private void peregrineUp(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();
        response.putHeader("Content-Type", "application/json");
        response.setStatusCode(200);
        response.end("{ \"Peregrine\" : \"All Systems Operational\" }");
    }

    private void statusPubHandler(final RoutingContext routingContext) {
        routingContext.request().bodyHandler(bodyHandler -> {
            final String body = new String(bodyHandler.getBytes(), StandardCharsets.UTF_8);
            System.out.println(body);
            vertx.eventBus().request(PUBLISH_STATUS_V1, body, event ->
                    routingContext.response().end(event.result().body().toString()));
        });
    }

}
