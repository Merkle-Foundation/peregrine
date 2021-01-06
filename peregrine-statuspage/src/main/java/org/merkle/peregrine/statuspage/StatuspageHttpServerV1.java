package org.merkle.peregrine.statuspage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.merkle.peregrine.statuspage.Routes.PEREGRINE_STATUS_V1;
import static org.merkle.peregrine.statuspage.Routes.PUBLISH_STATUS_V1;
import static org.merkle.peregrine.statuspage.Routes.PUBLISH_STATUS_VERTX_V1;

public class StatuspageHttpServerV1 extends AbstractVerticle {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void start() throws Exception {
        final HttpServer httpServer = vertx.createHttpServer();
        final HttpServer httpsServer = vertx.createHttpServer();

        final Router router = Router.router(vertx);
        router.route(HttpMethod.POST, PUBLISH_STATUS_V1).handler(this::statusPubHandler);
        router.route(HttpMethod.GET, PUBLISH_STATUS_V1).handler(this::statusPubHandler);
        router.route(HttpMethod.GET, PUBLISH_STATUS_V1 + "/*").handler(this::statusPubHandler);
        router.route(HttpMethod.GET, PEREGRINE_STATUS_V1).handler(this::peregrineUp);


        httpServer
                .requestHandler(router)
                .listen(80);

        httpsServer
                .requestHandler(router)
                .listen(443);
    }

    private void peregrineUp(final RoutingContext ctx) {
        ctx.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(200)
                .end("{ \"Peregrine\" : \"All Systems Operational\" }");
    }

    private void statusPubHandler(final RoutingContext ctx) {
        System.out.println("Received request at: " + new Date());

        ctx.request().bodyHandler(handler -> {
            try {
                final byte[] bytes = handler.getBytes();
                mapper.readTree(bytes);
                final String body = new String(bytes, StandardCharsets.UTF_8);
                vertx.eventBus().publish(PUBLISH_STATUS_VERTX_V1, body);

                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(200)
                        .end("{ \"Result\" : \"POST Message Consumed\" }");
            } catch (IOException e) {
                e.printStackTrace();
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(500)
                        .end("{ \"Result\" : \"POST Message Failed\" }");
            }
        });
    }

}
