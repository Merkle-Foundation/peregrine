package org.merkle.peregrine.statuspage.subscription;

public enum SubscriptionMedium {
    Email,
    Text,
    Discord,
    Slack,
    PagerDuty;

    public static SubscriptionMedium getValue(final String subscriptionMedium) {
        try {
            return SubscriptionMedium.valueOf(subscriptionMedium);
        } catch (Exception e) {
            return null;
        }
    }
}
