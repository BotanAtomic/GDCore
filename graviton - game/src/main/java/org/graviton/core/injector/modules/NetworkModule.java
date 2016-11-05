package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import org.graviton.network.exchange.ExchangeConnector;
import org.graviton.network.game.GameServer;
import org.graviton.network.game.handler.MessageHandler;

/**
 * Created by Botan on 04/11/2016 : 22:48
 */
public class NetworkModule extends AbstractModule {
    @Override
    protected void configure() {
        MessageHandler.initialize();
        bind(ExchangeConnector.class).asEagerSingleton();
        bind(GameServer.class).asEagerSingleton();
    }
}
