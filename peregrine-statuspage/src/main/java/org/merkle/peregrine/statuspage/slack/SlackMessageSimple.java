package org.merkle.peregrine.statuspage.slack;

public class SlackMessageSimple {

    private final String username;
    private final String content;

    public SlackMessageSimple(final String username, final String content) {
        this.username = username;
        this.content = content;
    }
}
