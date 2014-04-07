package ldf.parser;

import ldf.parser.ast.AstSourceFile;
import ldf.parser.inspect.Result;
import org.junit.Test;

import static org.junit.Assert.*;

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
    }

    @Test
    public void testParse2() throws Exception {
        beginTest();

        LdfParser parser = initParser("example2.txt");
        AstSourceFile result = parser.getAbstractSyntaxTree();
        assertNotNull(result);
        parser.scopeCheck();

        endTest();
    }

    @Test
    public void testAnalysis1() throws Exception {
        beginTest();

        LdfParser parser = initParser("example3.txt");
        assertNull(parser.getParseError());
        parser.syntaxCheck();

        endTest();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }
    }

    @Test
    public void testAnalysis2() throws Exception {
        beginTest();

        LdfParser parser = initParser("example4.txt");
        assertNull(parser.getParseError());
        parser.scopeCheck();

        endTest();
    }


}
