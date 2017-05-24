package org.graviton.lang;

/**
 * Created by Botan on 25/03/2017. 20:01
 */
class FrenchLanguage extends Language {

    FrenchLanguage() {
        super.put(LanguageSentence.BOUGHT_HOUSE, "Votre maison vient d'être acheté <i>#0</i> kamas par <b>#1</b>");
        super.put(LanguageSentence.NEED_DUNGEON_KEY, "Vous ne possédez pas la clef nécessaire");
        super.put(LanguageSentence.MERCHANT_BUSY,"Ce marchand est déjà en échange");
        super.put(LanguageSentence.MAX_IN_TRUNK,"Cet espace de stockage est rempli");
    }

}
