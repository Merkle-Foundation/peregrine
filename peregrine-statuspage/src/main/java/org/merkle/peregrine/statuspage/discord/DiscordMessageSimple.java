package org.merkle.peregrine.statuspage.discord;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.merkle.peregrine.core.Exchange;

public class DiscordMessageSimple {

    private final String username;
    private final String content;

    private DiscordMessageSimple(final String username, final String content) {
        this.username = username;
        this.content = content;
    }

    public String build() {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("username", username);
        node.put("content", content);
        return node.toPrettyString();
    }

    public static class DiscordStatusPageMessageBuilder {

        private String username;

        private final ObjectNode body = JsonNodeFactory.instance.objectNode();

        private final ObjectNode incident = JsonNodeFactory.instance.objectNode();

        public DiscordStatusPageMessageBuilder setExchange(final Exchange exchange) {
            username = exchange.name();
            body.put("exchange", username);
            return this;
        }

        public DiscordStatusPageMessageBuilder setIncidentName(final String name) {
            incident.put("name", name);
            return this;
        }

        public DiscordStatusPageMessageBuilder setIncidentStatus(final String status) {
            incident.put("status", status);
            return this;
        }

        public DiscordStatusPageMessageBuilder setBody(final String body) {
            incident.put("body", body);
            return this;
        }

        public DiscordStatusPageMessageBuilder setShortLink(final String shortlink) {
            incident.put("shortlink", shortlink);
            return this;
        }

        public DiscordMessageSimple build() {
            body.set("incident", incident);
            return new DiscordMessageSimple(username, body.toPrettyString());
        }
    }
}
