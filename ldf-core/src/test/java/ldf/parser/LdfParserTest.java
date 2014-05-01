package ldf.parser;

import ldf.CompilerLog;
import ldf.parser.ags.AgsNode;
import ldf.parser.ags.AgsNodeUnion;
import ldf.parser.ast.AstSourceFile;
import ldf.parser.ast.decl.DeclGrammar;
import ldf.parser.ast.decl.Declaration;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Cristian Harja
 */
public class LdfParserTest extends AbstractParserTest{

    @Test
    public void testParse1() throws Exception {
        beginTest();

        LdfParser parser = initParser("example1.txt");
        parser.parseInput();
        parser.getLogger().printLog(System.out);

        endTest();
        assertTrue(parser.successful());
        Thread.sleep(100);
    }

    @Test
    public void testParse2() throws Exception {
        beginTest();

        LdfParser parser = initParser("example2.txt");
        parser.parseInput();
        parser.getLogger().printLog(System.out);

        endTest();
        assertTrue(parser.successful());
        Thread.sleep(100);
    }

    @Test
    public void testParse3() throws Exception {
        beginTest();

        LdfParser parser = initParser("example3.txt");
        parser.syntaxCheck();
        parser.getLogger().printLog(System.out);

        endTest();
        assertTrue(parser.successful());
        Thread.sleep(100);
    }

    @Test
    public void testParse4() throws Exception {
        beginTest();

        LdfParser parser = initParser("example4.txt");
        parser.syntaxCheck();
        parser.getLogger().printLog(System.out);

        endTest();
        assertTrue(parser.successful());
        Thread.sleep(100);
    }

    // moved from `AstSourceFile`
    private static DeclGrammar findGrammar(
            AstSourceFile src, String name
    ) {
        for (Declaration d : src.getDeclarations().getItems()) {
            if (d instanceof DeclGrammar) {
                DeclGrammar g = (DeclGrammar) d;
                if (g.getId().getName().equals(name)) {
                    return g;
                }
            }
        }
        return null;
    }

    @Test
    public void testParse5() throws Exception {
        beginTest();

        LdfParser parser = initParser("example5.txt");
        parser.syntaxCheck();
        parser.getLogger().printLog(System.out);

        AstSourceFile root = parser.getAbstractSyntaxTree();
        DeclGrammar g = findGrammar(root, "Example5");
        AgsNodeUnion nterm = g.findNonTerm("A");

        endTest();
        assertTrue(parser.successful());

        for (CompilerLog.Entry r : parser.getResults()) {
            System.out.println(r);
        }

        for (AgsNode abstractProduction : nterm) {
            System.out.println(abstractProduction);
        }

        Thread.sleep(100);
    }

    @Test
    public void testParse6() throws Exception {
        beginTest();

        LdfParser parser = initParser("example6.txt");
        parser.parseInput();
        parser.syntaxCheck();
        parser.getLogger().printLog(System.out);

        endTest();
        assertTrue(parser.successful());
        Thread.sleep(100);
    }




}
