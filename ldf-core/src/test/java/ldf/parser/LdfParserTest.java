package ldf.parser;

import ldf.parser.ast.AstSourceFile;
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

        /*
        parser.scopeCheck();
        DeclGrammar example1 = result.getGrammar("Example1");
        DeclNonTerminal nterm = example1.getNterm("stmt_forever");
        BnfSyntaxDag.DagHandle handle = nterm.getSyntax().getDag();
        */
        endTest();
    }

    @Test
    public void testParse2() throws Exception {
        beginTest();

        LdfParser parser = initParser("example2.txt");
        AstSourceFile result = parser.getAbstractSyntaxTree();
        assertNotNull(result);
        parser.syntaxCheck();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }
        endTest();
    }

    @Test
    public void testAnalysis1() throws Exception {
        beginTest();

        LdfParser parser = initParser("example3.txt");
        assertNull(parser.getParseError());
        parser.syntaxCheck();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }
        endTest();
    }

    @Test
    public void testAnalysis2() throws Exception {
        beginTest();

        LdfParser parser = initParser("example4.txt");
        assertNull(parser.getParseError());
        parser.scopeCheck();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }
        endTest();
    }


}
