package org.merkle.peregrine.statuspage.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import org.merkle.peregrine.core.Exchange;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.merkle.peregrine.statuspage.Routes.CREATE_SUBSCRIPTION_VERTX_V1;

public class SubscriptionManager extends AbstractVerticle {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(CREATE_SUBSCRIPTION_VERTX_V1, this::processMessage);
    }

    /**
     * Builds the name which uniquely identifies a subscription
     *
     * @param topic of subscription
     * @param medium of subscription
     * @param exchange for subscription
     */
    public static String composeSubscriptionName(final SubscriptionTopic topic,
                                                 final SubscriptionMedium medium,
                                                 final Exchange exchange) {
        return medium.name().toLowerCase() + "_" + topic.name().toLowerCase() + "_" + exchange.name().toLowerCase();
    }

    /**
     * Processes request for a new subscription
     *
     * @param message containing subscription request details
     */
    private void processMessage(final Message<?> message) {

        try {
            final JsonNode msg = mapper.readValue(message.body().toString(), JsonNode.class);
            if (!validSubscription(message, msg)) {
                return;
            }
            final Exchange exchange = Exchange.getValue(msg.get("exchange").asText().toUpperCase());
            final SubscriptionTopic topic = SubscriptionTopic.getValue(msg.get("topic").asText().toUpperCase());
            final SubscriptionMedium medium = SubscriptionMedium.getValue(msg.get("medium").asText().toUpperCase());
            final String medium_value = msg.get("medium_value").asText();

            final String result = String.format("Subscribed [%s] via [%s] for exchange [%s] and topic [%s] ",
                    medium_value, medium, exchange, topic);

            final String mapName = composeSubscriptionName(topic, medium, exchange);
            final LocalMap<String, JsonObject> map = vertx.sharedData().getLocalMap(mapName);
            final JsonObject subscribers = map.computeIfAbsent(mapName, s -> new JsonObject());
            if (subscribers.containsKey(medium_value)) {
                successResponse(message, String.format("[%s] already has [%s] as a subscriber",
                        mapName.toUpperCase(), medium_value));
                return;
            }
            subscribers.put(medium_value, medium_value);

            successResponse(message, result);
        } catch (JsonProcessingException e) {
            errorResponse(message, "Error processing json");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            errorResponse(message,  sw.toString());
        }
    }

    /**
     * Validates if a subscription body has all required details
     *
     * @param message to respond to
     * @param msg body of request
     * @return if valid
     */
    private boolean validSubscription(final Message<?> message, final JsonNode msg) {
        if (!msg.has("medium")) {
            errorResponse(message, "Missing the subscription [medium]");
            return false;
        }
        final SubscriptionMedium medium = SubscriptionMedium.getValue(msg.get("medium").asText().toUpperCase());
        if (medium == null) {
            errorResponse(message, "Invalid subscription [medium]");
            return false;
        }
        if (!msg.has("medium_value") || msg.get("medium_value").asText().isBlank()) {
            errorResponse(message, "Missing the subscription [medium_value]");
            return false;
        }
        if (!msg.has("topic")) {
            errorResponse(message, "Missing the subscription [topic]");
            return false;
        }
        final SubscriptionTopic topic = SubscriptionTopic.getValue(msg.get("topic").asText().toUpperCase());
        if (topic == null) {
            errorResponse(message, "Invalid subscription [topic]");
            return false;
        }
        if (!msg.has("exchange")) {
            errorResponse(message, "Missing the subscription [exchange]");
            return false;
        }
        final Exchange exchange = Exchange.getValue(msg.get("exchange").asText().toUpperCase());
        if (exchange == null) {
            errorResponse(message, "Invalid subscription [exchange]");
            return false;
        }
        return true;
    }

    private void successResponse(final Message<?> message, final String msg) {
        final ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.put("message", msg);
        response.put("create_subscription", "succeeded");
        message.reply(response.toString());
    }

    private void errorResponse(final Message<?> message, final String errorMsg) {
        final ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.put("message", errorMsg);
        response.put("create_subscription", "failed");
        message.fail(500, response.toString());
    }
}
