package org.merkle.peregrine.statuspage.discord;

public class DiscordMessageSimple {

    private final String username;
    private final String content;

    public DiscordMessageSimple(final String username, final String content) {
        this.username = username;
        this.content = content;
    }
}
