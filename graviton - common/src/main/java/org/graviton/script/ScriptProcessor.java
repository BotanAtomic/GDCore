package org.graviton.script;

import lombok.extern.slf4j.Slf4j;
import org.mozilla.javascript.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by Botan on 13/04/2017. 22:46
 */

@Slf4j
public class ScriptProcessor {
    private Scriptable scope;

    public ScriptProcessor() {
        Context context = Context.enter();
        try {
            context.setLanguageVersion(Context.VERSION_1_8);
            context.setOptimizationLevel(3);
            context.getWrapFactory().setJavaPrimitiveWrap(false);
            scope = new ImporterTopLevel(context);
            log.debug("Script processor successfully loaded");
        } finally {
            Context.exit();
        }
    }

    public void importElement(Object value, String name) {
        Context.enter();
        try {
            ScriptableObject.putProperty(scope, name, Context.javaToJS(value, scope));
        } finally {
            Context.exit();
        }
    }

    public void loadPath(String path) {
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.filter(Files::isRegularFile).forEach(filePath -> executeScript(filePath.toString()));
        } catch (Exception e) {
            log.error("cannot load grouped script [{}] -> {}", path, e.getMessage());
        }
    }

    public String execute(String command) {
        System.err.println("Execute " + command);
        Context context = Context.enter();
        try {
            return String.valueOf(context.evaluateString(scope, command, "<intern>", 0 , null));
        } catch (RhinoException e) {
            return e.getMessage();
        } finally {
            Context.exit();
        }
    }


    private void executeScript(String fileName) {
        Context context = Context.enter();
        try {
            FileReader fileReader = new FileReader(fileName);
            context.evaluateReader(scope, fileReader, fileName, 1, null);
            fileReader.close();
        } catch (IOException e) {
            log.error("cannot load script [{}] -> {}", fileName, e.getMessage());
        } finally {
            Context.exit();
        }
    }


}
