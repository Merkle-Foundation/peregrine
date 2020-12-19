package org.merkle.peregrine.statuspage;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class SimpleEntry {

    public static void main(String[] args) {

        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(StatuspageHttpServerV1.class.getName(), new DeploymentOptions()
            .setWorker(true)
            .setInstances(2), event -> {

        });
    }
}
