package org.merkle.peregrine.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PeregrineConfig {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, String> discordHooks;
    private final Map<String, String> slackHooks;

    public PeregrineConfig(final Map<String, String> discordHooks,
                           final Map<String, String> slackHooks) {
        this.discordHooks = discordHooks;
        this.slackHooks = slackHooks;
    }

    public static PeregrineConfig loadConfig(final String path) throws IOException {
        final JsonNode config = mapper.readValue(
                new FileInputStream(new File(path)), JsonNode.class);

        final ObjectNode discord = (ObjectNode) config.get("discord");
        final Map<String, String> discordHooks = new HashMap<>();
        final Iterator<String> discordFields = discord.fieldNames();
        while(discordFields.hasNext()) {
            final String exchangeStr = discordFields.next();
            final Exchange exchange = Exchange.valueOf(exchangeStr.toUpperCase());
            discordHooks.put(exchange.statusPageId, discord.get(exchangeStr).asText());
        }

        final ObjectNode slack = (ObjectNode) config.get("slack");
        final Map<String, String> slackHooks = new HashMap<>();
        final Iterator<String> slackFields = slack.fieldNames();
        while(slackFields.hasNext()) {
            final String exchangeStr = slackFields.next();
            final Exchange exchange = Exchange.valueOf(exchangeStr.toUpperCase());
            slackHooks.put(exchange.name(), slack.get(exchangeStr).asText());
        }

        return new PeregrineConfig(discordHooks, slackHooks);
    }

    public Map<String,String> getDiscordHooks() {
        return discordHooks;
    }

    public Map<String,String> getSlackHooks() {
        return slackHooks;
    }
}
