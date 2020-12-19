package org.merkle.peregrine.statuspage.discord;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

import static org.merkle.peregrine.statuspage.Routes.PUBLISH_STATUS_VERTX_V1;

public class DiscordConsumer extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(PUBLISH_STATUS_VERTX_V1, this::successResponse);
    }

    private void successResponse(final Message<?> message) {
        System.out.println("Discord consumer processed: " + message.body().toString());
    }
}
