package org.graviton.network.login.handler;

import org.graviton.api.AbstractHandler;
import org.graviton.network.login.LoginClient;

/**
 * Created by Botan on 30/10/2016 : 01:42
 */
public class PlayerSelectionHandler extends AbstractHandler {

    @Override
    public void handle(String data, LoginClient client) {
        switch (data.substring(0, 2)) {
            case "AF":
                break;
            case "Af":
                break;
            case "AX":
                break;
            case "Ax":
                break;
        }
    }

}
