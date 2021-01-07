package org.merkle.peregrine.statuspage.discord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import org.merkle.peregrine.core.Exchange;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static org.merkle.peregrine.statuspage.Routes.PUBLISH_STATUS_VERTX_V1;

public class DiscordConsumer extends AbstractVerticle {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, String> discordHook;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public DiscordConsumer(final Map<String, String> discordHook) {
        this.discordHook = discordHook;
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(PUBLISH_STATUS_VERTX_V1, this::consumeMessage);
    }

    private void consumeMessage(final Message<?> message) {
        try {
            final String msgStr = message.body().toString();
            final JsonNode msg = mapper.readTree(msgStr);
            if (msg.has("page") && msg.get("page").has("id")) {
                final String statusPageId = msg.get("page").get("id").asText();
                final Exchange exchange = Exchange.reverseMap(statusPageId);
                if (exchange == null) {
                    System.out.printf("statusPageId: %s is unknown%n", statusPageId);
                    return;
                }
                final URI uri = URI.create(discordHook.get(statusPageId));
                final DiscordMessageSimple discordMsg = buildDiscordMessage(msg, exchange);
                if (discordMsg == null) {
                    return;
                }
                postToDiscord(discordMsg.build(), uri);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private DiscordMessageSimple buildDiscordMessage(final JsonNode incident, final Exchange exchange) {
        final DiscordMessageSimple.DiscordStatusPageMessageBuilder builder =
                new DiscordMessageSimple.DiscordStatusPageMessageBuilder();
        builder.setExchange(exchange);

        if (incident.has("incident")) {
            if (incident.get("incident").has("name")) {
                builder.setIncidentName(incident.get("incident").get("name").asText());
            }
            if (incident.get("incident").has("status")) {
                builder.setIncidentStatus(incident.get("incident").get("status").asText());
            }
            if (incident.get("incident").has("shortlink")) {
                builder.setShortLink(incident.get("incident").get("shortlink").asText());
            }
            if (incident.get("incident").has("incident_updates")) {
                final ArrayNode incidentUpdates = (ArrayNode)incident.get("incident").get("incident_updates");
                final JsonNode latestUpdate = incidentUpdates.get(0);
                if (latestUpdate.has("body")) {
                    builder.setBody(latestUpdate.get("body").asText());
                }
            }
            return builder.build();
        }
        return null;
    }

    private void postToDiscord(final String msg, final URI uri) throws IOException, InterruptedException {
        httpClient.send(
                HttpRequest.newBuilder()
                        .uri(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(msg))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
