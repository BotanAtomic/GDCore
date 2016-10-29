package org.graviton.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.SQLException;

/**
 * Created by Botan on 29/10/2016 : 04:43
 */
public class LoginDatabase {

    private HikariDataSource dataSource;

    private DSLContext dslContext;

    public LoginDatabase() {
        this.dataSource = new HikariDataSource(new HikariConfig() {{
            setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
            addDataSourceProperty("url", System.getProperties().getProperty("database.url"));
            addDataSourceProperty("port", 3306);
            addDataSourceProperty("user", System.getProperties().getProperty("database.user"));
            addDataSourceProperty("password", System.getProperties().getProperty("database.password"));
        }});

    }

    public void connect() throws SQLException {
        this.dslContext = DSL.using(dataSource.getConnection(), SQLDialect.MYSQL);
    }

    public String getJdbcUrl() {
        return dataSource.getDataSourceProperties().getProperty("url");
    }

}
