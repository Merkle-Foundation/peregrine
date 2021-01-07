package org.merkle.peregrine.statuspage;

import io.vertx.core.Vertx;
import org.merkle.peregrine.core.PeregrineConfig;
import org.merkle.peregrine.statuspage.discord.DiscordConsumer;
import org.merkle.peregrine.statuspage.slack.SlackConsumer;
import org.merkle.peregrine.statuspage.subscription.SubscriptionManager;

import java.io.IOException;

public class SimpleEntry {

    public static void main(String[] args) throws IOException {

        final Vertx vertx = Vertx.vertx();

        final PeregrineConfig cfg = PeregrineConfig.loadConfig("");

        vertx.deployVerticle(new SlackConsumer());
        vertx.deployVerticle(new DiscordConsumer(cfg.getDiscordHooks()));
        vertx.deployVerticle(new SubscriptionManager());
        vertx.deployVerticle(new StatuspageHttpServerV1());
    }
}
