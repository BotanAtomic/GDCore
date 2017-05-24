package org.graviton.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.Manageable;
import org.graviton.core.Program;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Botan on 02/11/2016 : 13:18
 */
@Slf4j
public class Database implements Manageable {
    private final HikariDataSource dataSource;

    @Getter
    private DSLContext dslContext;


    public Database(Properties properties, Program program) {
        program.register(this);
        this.dataSource = new HikariDataSource(new HikariConfig(properties));
    }

    @Override
    public void start() {
        try {
            dslContext = DSL.using(dataSource.getConnection(), SQLDialect.MYSQL);
            log.info("connected to database server [{}]", dataSource.getDataSourceProperties().getProperty("url"));
        } catch (SQLException e) {
            log.error("Unable to connect to database {} [cause:{}]", dataSource.getDataSourceProperties().getProperty("url"), e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            this.dataSource.getConnection().close();
            this.dslContext.close();
            log.debug("Database closed");
        } catch (SQLException e) {
            log.error("Unable to close database [cause:{}]", e.getMessage());
        }
    }

    @Override
    public byte index() {
        return 0;
    }


    public Record getRecord(Table<?> table, Condition... condition) {
        return dslContext.select().from(table).where(condition).fetchOne();
    }

    public Result<Record> getResult(Table<?> table) {
        return dslContext.select().from(table).fetch();
    }

    public Result<Record> getResult(Table<?> table, Condition... condition) {
        return dslContext.select().from(table).where(condition).fetch();
    }

    public UpdateSetFirstStep<?> update(Table<?> table) {
        return dslContext.update(table);
    }

    public int getNextId(Table<?> table, Field<?> field) {
        try {
            return (int) dslContext.select((field.max()).add(1)).from(table).fetchAny().get(0);
        } catch (Exception e) {
            return 1;
        }
    }

}
