package org.graviton.network.game.protocol;

import org.graviton.game.hdv.SellPoint;

/**
 * Created by Botan on 26/06/17. 23:25
 */
public class SellPointPacketFormatter {

    public static String startMessage(SellPoint sellPoint) {
        return "ECK11|" + "1,10,100;" + sellPoint.getCategoriesData() + ";" + sellPoint.getTax() + ";" + sellPoint.getLevel() + ";100;-1;" + sellPoint.getExpiration();

    }

}
