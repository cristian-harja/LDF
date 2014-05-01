package ldf.compiler;

import java.io.File;

/**
 * @author Cristian Harja
 */
public abstract class AbstractCompilerTest {

    protected LdfCompiler initCompiler(String name) throws Exception {
        LdfCompilerSettings settings = new LdfCompilerSettings();
        File root = new File("ldf-core/src/test/ldf");
        if (!root.exists()) {
            root = new File("src/test/ldf");
            if (!root.exists()) {
                throw new RuntimeException("Can't find test files");
            }
        }
        File path = new File(root, name);
        if (path.isFile()) {
            settings.addSourceFile(path);
        } else if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files == null) return null;
            for (File f : files) {
                settings.addSourceFile(f);
            }
        }
        return new LdfCompiler(settings);
    }

}
