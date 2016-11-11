package org.graviton.game.look;

/**
 * Created by Botan on 11/11/2016 : 21:35
 */
public abstract class Look {
    private String name;
    private int[] colors;
    private short skin;

    public Look(String name, int[] colors, short skin) {
        this.name = name;
        this.colors = colors;
        this.skin = skin;
    }

}
