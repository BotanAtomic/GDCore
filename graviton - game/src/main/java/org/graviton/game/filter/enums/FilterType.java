package org.graviton.game.filter.enums;

import lombok.Getter;

/**
 * Created by Botan on 27/12/2016. 14:31
 */
public enum FilterType {
    EQUALS('='),
    MORE('>'),
    LESS('<'),
    DIFFERENT('!');

    @Getter
    private final char sign;

    FilterType(char sign) {
        this.sign = sign;
    }

    public static FilterType get(char value) {
        for (FilterType type : values())
            if (type.sign == value)
                return type;
        return null;
    }
}
