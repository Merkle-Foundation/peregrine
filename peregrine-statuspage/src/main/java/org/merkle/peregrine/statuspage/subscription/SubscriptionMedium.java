package org.merkle.peregrine.statuspage.subscription;

public enum SubscriptionMedium {
    EMAIL,
    TEXT,
    DISCORD,
    SLACK,
    PAGERDUTY;

    public static SubscriptionMedium getValue(final String subscriptionMedium) {
        try {
            return SubscriptionMedium.valueOf(subscriptionMedium);
        } catch (Exception e) {
            return null;
        }
    }
}
