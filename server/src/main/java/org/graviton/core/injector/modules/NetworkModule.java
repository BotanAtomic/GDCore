package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import org.graviton.network.exchange.ExchangeServer;
import org.graviton.network.login.LoginServer;

/**
 * Created by Botan on 29/10/2016 : 06:52
 */
public class NetworkModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LoginServer.class).asEagerSingleton();
        bind(ExchangeServer.class).asEagerSingleton();
    }
}
