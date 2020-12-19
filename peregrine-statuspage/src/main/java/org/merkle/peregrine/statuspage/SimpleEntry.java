package org.merkle.peregrine.statuspage;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.merkle.peregrine.statuspage.discord.DiscordConsumer;
import org.merkle.peregrine.statuspage.slack.SlackConsumer;

public class SimpleEntry {

    public static void main(String[] args) {

        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new SlackConsumer());
        vertx.deployVerticle(new DiscordConsumer());

        vertx.deployVerticle(StatuspageHttpServerV1.class.getName(), new DeploymentOptions()
            .setWorker(true)
            .setInstances(2), event -> {

        });
    }
}
