package org.graviton.database;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.Manageable;
import org.graviton.core.Server;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Botan on 29/10/2016 : 04:43
 */

@Slf4j
public class LoginDatabase implements Manageable {
    private HikariDataSource dataSource;

    private DSLContext dslContext;

    @Inject
    public LoginDatabase(Server server, Properties properties) {
        server.add(this);
        this.dataSource = new HikariDataSource(new HikariConfig(properties));
    }

    @Override
    public void start() {
        try {
            this.dslContext = DSL.using(dataSource.getConnection(), SQLDialect.MYSQL);
            log.info("Connected successfully to database server [{}]", dataSource.getDataSourceProperties().getProperty("url"));
        } catch (SQLException e) {
            log.error("Unable to connect to database [cause:{}]", dataSource.getDataSourceProperties().getProperty("url"), e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            this.dataSource.getConnection().close();
            this.dslContext.close();
            log.debug("Database successfully closed");
        } catch (SQLException e) {
            log.error("Unable to close database [cause:{}]", e.getMessage());
        }
    }
}
