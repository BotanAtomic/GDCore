package org.graviton.lang;

import java.util.HashMap;

/**
 * Created by Botan on 05/11/2016 : 22:40
 */
public abstract class Language extends HashMap<LanguageSentence, String> {


    public String getSentence(LanguageSentence boughtHouse, String... argument) {
        String firstValue = super.get(boughtHouse);

        if (argument != null)
            for (int i = 0; i < argument.length; i++)
                firstValue = firstValue.replace("#" + i, argument[i]);

        return firstValue;
    }

    public static Language get(String language) {
        switch (language) {
            case "fr":
                return new FrenchLanguage();
            case "en":
                return new EnglishLanguage();
            case "es":
                return new SpanishLanguage();
        }
        return new EnglishLanguage();
    }
}


