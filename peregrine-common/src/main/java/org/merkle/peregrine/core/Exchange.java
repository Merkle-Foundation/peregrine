package org.merkle.peregrine.core;


public enum Exchange {
    COINBASE("kr0djjh0jyy9"),
    KRAKEN("lfz25gyhcpjf"),
    HUOBI("p0qjfl24znv5"),
    BITMEX("hrfrk46yvvdq"),
    BITFINEX("d9v90pqn3hl2");

    public final String statusPageId;

    Exchange(final String statusPageId) {
        this.statusPageId = statusPageId;
    }

    public static Exchange reverseMap(final String statusPageId) {
        for(Exchange e : Exchange.values()) {
            if (e.statusPageId.equals(statusPageId)) {
                return e;
            }
        }
        return null;
    }
}
