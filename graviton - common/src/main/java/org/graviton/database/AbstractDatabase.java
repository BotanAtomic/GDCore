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

import static org.jooq.impl.DSL.max;

/**
 * Created by Botan on 02/11/2016 : 13:18
 */
@Slf4j
public abstract class AbstractDatabase implements Manageable {
    private final HikariDataSource dataSource;

    @Getter
    public DSLContext dslContext;

    public AbstractDatabase(Properties properties, Program program) {
        program.add(this);
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


    public Record getRecord(Table<?> table, Condition condition) {
        return dslContext.select().from(table).where(condition).fetchOne();
    }

    public Result<Record> getResult(Table<?> table) {
        return dslContext.select().from(table).fetch();
    }

    public Result<Record> getResult(Table<?> table, Condition condition) {
        return dslContext.select().from(table).where(condition).fetch();
    }

    public UpdateSetFirstStep<?> update(Table<?> table) {
        return dslContext.update(table);
    }

    public int getNextId(Table<?> table, Field<?> field) {
        try {
            return (int) dslContext.select(max(field).add(1)).from(table).fetchOne().getValue(0);
        } catch (Exception e) {
            return 1;
        }
    }

}
