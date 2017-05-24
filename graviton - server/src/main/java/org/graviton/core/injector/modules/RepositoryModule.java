package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import org.graviton.database.repository.AccountRepository;
import org.graviton.database.repository.GameServerRepository;

/**
 * Created by Botan on 29/10/2016 : 06:52
 */
public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountRepository.class).asEagerSingleton();
        bind(GameServerRepository.class).asEagerSingleton();
    }
}
