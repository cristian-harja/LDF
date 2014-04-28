package ldf.parser;

import ldf.parser.ags.AgsNode;
import ldf.parser.ags.AgsNodeUnion;
import ldf.parser.ast.AstSourceFile;
import ldf.parser.ast.decl.DeclGrammar;
import ldf.parser.inspect.Result;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Cristian Harja
 */
public class LdfParserTest extends AbstractParserTest{

    @Test
    public void testParse1() throws Exception {
        beginTest();

        LdfParser parser = initParser("example1.txt");
        AstSourceFile result = parser.getAbstractSyntaxTree();
        assertNotNull(result);

        endTest();
        Thread.sleep(100);
    }

    @Test
    public void testParse2() throws Exception {
        beginTest();

        LdfParser parser = initParser("example2.txt");
        AstSourceFile result = parser.getAbstractSyntaxTree();
        assertNotNull(result);

        endTest();
        Thread.sleep(100);
    }

    @Test
    public void testParse3() throws Exception {
        beginTest();

        LdfParser parser = initParser("example3.txt");
        assertNull(parser.getParseError());
        parser.syntaxCheck();

        endTest();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }

        Thread.sleep(100);
    }

    @Test
    public void testParse4() throws Exception {
        beginTest();

        LdfParser parser = initParser("example4.txt");
        assertNull(parser.getParseError());
        parser.syntaxCheck();

        endTest();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }

        Thread.sleep(100);
    }

    @Test
    public void testParse5() throws Exception {
        beginTest();

        LdfParser parser = initParser("example5.txt");
        assertNull(parser.getParseError());
        parser.syntaxCheck();

        AstSourceFile root = parser.getAbstractSyntaxTree();
        DeclGrammar g = root.findGrammar("Example5");
        AgsNodeUnion nterm = g.findNonTerm("A");

        endTest();

        for(Result r: parser.getResults()) {
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
        assertNull(parser.getParseError());
        parser.parseInput();

        endTest();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }

        Thread.sleep(100);
    }




}
