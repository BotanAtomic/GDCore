package org.graviton.core;


import org.graviton.core.injector.MainModule;

import java.io.IOException;

/**
 * Created by Botan on 02/11/2016 : 03:24
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Application.create(new MainModule(), System.currentTimeMillis());
    }

}


