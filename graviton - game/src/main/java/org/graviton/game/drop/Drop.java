package org.graviton.game.drop;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Botan on 04/02/2017. 10:54
 */

@Data
public class Drop {
    private final short item, ceil;
    private final int monster;
    private final double[] chance;
    private final boolean unique, meat;
    private double finalChance;
    private int[] maps;

    private byte jobLevel;

    public Drop(XMLElement element) {
        this.item = element.getAttribute("item").toShort();
        this.ceil = element.getAttribute("ceil").toShort();
        this.monster = element.getAttribute("monster").toInt();
        this.unique = element.getAttribute("unique").toBoolean();
        this.meat = element.getAttribute("meat").toBoolean();
        this.chance = new double[5];
        if (!element.getElementByTagName("condition", "level").toString().isEmpty())
            this.jobLevel = element.getElementByTagName("condition", "level").toByte();


        IntStream.range(0, 5).forEach(i -> chance[i] = element.getElementByTagName("percent", "grade" + (i + 1)).toDouble());
    }

    private Drop(Drop drop, byte monsterGrade) {
        this.item = drop.item;
        this.monster = drop.monster;
        this.ceil = drop.ceil;
        this.chance = Arrays.copyOf(drop.chance, 5);
        System.err.println("Monster grade = " + monsterGrade);
        System.err.println("Chance lenght = " + drop.chance.length);
        this.finalChance = getFinalChance(drop, monsterGrade);
        this.unique = drop.unique;
        this.meat = drop.meat;
        this.maps = drop.maps;
        this.jobLevel = drop.jobLevel;
    }

    private double getFinalChance(Drop drop, byte monsterGrade) {
        if (drop.chance.length == 0)
            return 0;

        return (drop.chance.length < monsterGrade - 1) ? drop.chance[drop.chance.length - 1] : drop.chance[monsterGrade - 1];
    }

    public boolean validate(Player player, short totalProspection) {
        if (ceil > totalProspection)
            return false;

        //todo : job
        if (jobLevel > 0)
            return false;

        double prospection = (double) player.getProspection() / 100;
        return Utils.limit(finalChance * (prospection < 1 ? 1 : prospection), 95) >= ThreadLocalRandom.current().nextDouble(0, 100);
    }

    public Drop copy(byte monsterGrade) {
        return new Drop(this, monsterGrade);
    }
}
