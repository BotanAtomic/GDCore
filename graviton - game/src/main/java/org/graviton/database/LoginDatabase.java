package org.graviton.database;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.core.Program;

import java.util.Properties;

/**
 * Created by Botan on 04/11/2016 : 22:24
 */
public class LoginDatabase extends AbstractDatabase {
    @Inject
    public LoginDatabase(@Named("database.login.properties") Properties properties, Program program) {
        super(properties, program);
    }
}