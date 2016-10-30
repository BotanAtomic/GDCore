package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.LoginDatabase;
import org.graviton.database.repository.AccountRepository;

/**
 * Created by Botan on 29/10/2016 : 04:24
 */
@Slf4j
public class DatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LoginDatabase.class).asEagerSingleton();
        bind(AccountRepository.class).asEagerSingleton();
    }
}
