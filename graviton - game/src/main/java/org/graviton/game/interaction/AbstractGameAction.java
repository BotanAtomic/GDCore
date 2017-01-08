package org.graviton.game.interaction;


/**
 * Created by Botan on 16/11/2016 : 21:05
 */
public interface AbstractGameAction {
    boolean begin();

    void cancel(String data);

    void finish(String data);

}
