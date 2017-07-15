package org.graviton.network.game.protocol;

/**
 * Created by Botan on 15/07/17. 20:13
 */
public class InteractiveDoorPacketFormatter {

    public static String cellUpdateMessage(short cell, boolean walkable) {
        return "GDC" + +cell + (walkable ? ";aaGaaaaaaa801;1" : ";aaaaaaaaaa801;1");
    }

    public static String doorActionMessage(short id, boolean open, boolean fast) {
        return "GDF|" + id + (!fast ? (open ? ";2" : ";4") : (open ? ";3" : ";1"));

    }

}
