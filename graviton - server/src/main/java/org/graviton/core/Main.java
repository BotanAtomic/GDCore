package org.graviton.core;


import org.graviton.core.injector.MainModule;

/**
 * Created by Botan on 29/10/2016 : 03:09
 */
public class Main {

    public static void main(String[] args) {
        Application.create(new MainModule(), System.currentTimeMillis());
    }

}
