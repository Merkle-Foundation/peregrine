package org.merkle.peregrine.statuspage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.merkle.peregrine.statuspage.Routes.CREATE_SUBSCRIPTION_V1;
import static org.merkle.peregrine.statuspage.Routes.CREATE_SUBSCRIPTION_VERTX_V1;
import static org.merkle.peregrine.statuspage.Routes.PEREGRINE_WEB_ROOT;
import static org.merkle.peregrine.statuspage.Routes.PEREGRINE_STATUS_V1;
import static org.merkle.peregrine.statuspage.Routes.STATUS_PAGE_PUBLISH_V1;
import static org.merkle.peregrine.statuspage.Routes.PUBLISH_STATUS_VERTX_V1;

public class StatuspageHttpServerV1 extends AbstractVerticle {

    private FreeMarkerTemplateEngine engine;

    @Override
    public void start() {
        final HttpServer httpServer = vertx.createHttpServer();
        final HttpServer httpsServer = vertx.createHttpServer();

        final Router router = Router.router(vertx);
        router.route(HttpMethod.GET, PEREGRINE_WEB_ROOT).handler(this::peregrineWeb);
        router.route(HttpMethod.GET, PEREGRINE_STATUS_V1).handler(this::peregrineUp);
        router.route(HttpMethod.POST, STATUS_PAGE_PUBLISH_V1).handler(this::statusPubHandler);
        router.route(HttpMethod.POST, CREATE_SUBSCRIPTION_V1).handler(this::createSubHandler);

        engine = FreeMarkerTemplateEngine.create(vertx);
        
        httpServer
                .requestHandler(router)
                .listen(80);

        httpsServer
                .requestHandler(router)
                .listen(443);
    }
    private void peregrineWeb(final RoutingContext ctx) {
        JsonObject data = new JsonObject()
            .put("name", "Peregrine")
            .put("path", ctx.request().path());

        engine.render(data, "templates/index.ftl", res -> {
            if (res.succeeded())
                ctx.response().end(res.result());
            else
                ctx.fail(res.cause());
        });

        ctx.response()
            .putHeader("Content-Type", "text/html")
            .setChunked(true)
            .setStatusCode(200)
            .end("Hello Peregrine");
    }

    private void peregrineUp(final RoutingContext ctx) {
        ctx.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(200)
                .end("{ \"Peregrine\" : \"All Systems Operational\" }");
    }

    /**
     * Handler for inbound messages posted from https://www.atlassian.com/software/statuspage
     */
    private void statusPubHandler(final RoutingContext ctx) {
        System.out.println("Received request at: " + new Date());

        ctx.request().bodyHandler(handler -> {
            final String body = new String(handler.getBytes(), StandardCharsets.UTF_8);
            System.out.println("Inbound statuspage message: " + body);
            vertx.eventBus().publish(PUBLISH_STATUS_VERTX_V1, body);

            ctx.response()
                    .putHeader("Content-Type", "application/json")
                    .setStatusCode(200)
                    .end("{ \"Result\" : \"POST Message Consumed\" }");
        });
    }

    /**
     * Handler for a user requested subscription to one of our topics
     */
    public void createSubHandler(final RoutingContext ctx) {
        ctx.request().bodyHandler(handler -> {
            final String body = new String(handler.getBytes(), StandardCharsets.UTF_8);
            vertx.eventBus().request(CREATE_SUBSCRIPTION_VERTX_V1, body, event -> {
                if (event.succeeded()) {
                    ctx.response()
                            .setStatusCode(200)
                            .end(event.result().body().toString());
                } else {
                    ctx.response()
                            .setStatusCode(500)
                            .end(event.cause().getMessage());
                }
            });
        });
    }
}
