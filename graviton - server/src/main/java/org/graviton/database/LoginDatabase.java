package org.graviton.database;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.Manageable;
import org.graviton.core.Program;

import java.util.Properties;

/**
 * Created by Botan on 29/10/2016 : 04:43
 */

@Slf4j
public class LoginDatabase extends AbstractDatabase implements Manageable {
    @Inject
    public LoginDatabase(Program program, Properties properties) {
        super(properties);
        program.add(this);
    }

}
