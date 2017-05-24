package org.graviton.game.exchange;

/**
 * Created by Botan on 03/03/2017. 23:30
 */
public interface Exchange {

    void accept();

    void cancel();

    short getItemQuantity(int exchangerId, int itemId);

    void addItem(int itemId, short quantity, int exchangerId);

    void removeItem(int itemId, short quantity, int exchangerId);

    void editKamas(long quantity, int exchangerId);

    void toggle(int exchangerId);

    void buy(int itemId, short quantity);

    void sell(int itemId, short quantity);

}
