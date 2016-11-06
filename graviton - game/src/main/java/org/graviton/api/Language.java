package org.graviton.api;

/**
 * Created by Botan on 05/11/2016 : 22:40
 */
public enum Language {
    FRENCH,
    SPANISH,
    ENGLISH;

    public static Language get(String language) {
        switch (language) {
            case "fr":
                return FRENCH;
            case "en":
                return ENGLISH;
            case "es":
                return SPANISH;
        }
        return ENGLISH;
    }
}
