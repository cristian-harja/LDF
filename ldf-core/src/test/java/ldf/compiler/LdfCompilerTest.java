package ldf.compiler;

import org.junit.Test;

/**
 * @author Cristian Harja
 */
public class LdfCompilerTest extends AbstractCompilerTest {

    @Test
    public void testCompile1() throws Exception {
        LdfCompiler compiler = initCompiler("testCompile1");
        compiler.parseAllFiles();
        compiler.compileParsedFiles();
        compiler.getLogger().printLog(System.out);
        Thread.sleep(200);
    }

}
