package org.graviton.core.injector.modules;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.LoginDatabase;

import java.sql.SQLException;

/**
 * Created by Botan on 29/10/2016 : 04:24
 */
@Slf4j
public class DatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        LoginDatabase loginDatabase;

        bind(LoginDatabase.class).toInstance((loginDatabase = new LoginDatabase()));

        try {
            loginDatabase.connect();
            log.info("Connected successfully to database server [{}]", loginDatabase.getJdbcUrl());
        } catch (SQLException e) {
            super.addError(e);
        }
    }
}
