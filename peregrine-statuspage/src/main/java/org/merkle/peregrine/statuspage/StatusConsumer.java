package org.merkle.peregrine.statuspage;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

import static org.merkle.peregrine.statuspage.Routes.PUBLISH_STATUS_VERTX_V1;

public class StatusConsumer extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(PUBLISH_STATUS_VERTX_V1, this::successResponse);
    }

    private void successResponse(final Message<?> message) {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("Message", "Consumed: " + message.body().toString());
        message.reply(node.toString());
    }


}
