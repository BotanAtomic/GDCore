package org.graviton.game.channel;

/**
 * Created by Botan on 06/12/2016. 18:52
 */
public enum Channel {
    Alignment('!'),
    Team('#'),
    Party('$'),
    Guild('%'),
    General('*'),
    Trade(':'),
    Recruitment('?'),
    Admin('@'),
    Information('i');

    private char value;

    Channel(char value) {
        this.value = value;
    }

    public static Channel get(char value) {
        for (Channel channel : values())
            if (channel.value == value)
                return channel;
        return null;
    }

    public char value() {
        return value;
    }
}
