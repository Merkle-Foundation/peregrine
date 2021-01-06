package org.merkle.peregrine.statuspage.slack;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

import static org.merkle.peregrine.statuspage.Routes.PUBLISH_STATUS_VERTX_V1;

public class SlackConsumer extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(PUBLISH_STATUS_VERTX_V1, this::successResponse);
    }

    private void successResponse(final Message<?> message) {}
}
