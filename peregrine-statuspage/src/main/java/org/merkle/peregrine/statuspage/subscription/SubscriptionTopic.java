package org.merkle.peregrine.statuspage.subscription;

public enum SubscriptionTopic {
    REST,
    WS,
    STATUSPAGE;

    public static SubscriptionTopic getValue(final String subscriptionTopic) {
        try {
            return SubscriptionTopic.valueOf(subscriptionTopic);
        } catch (Exception e) {
            return null;
        }
    }
}
