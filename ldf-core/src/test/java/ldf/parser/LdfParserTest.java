package ldf.parser;

import ldf.parser.inspect.Result;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * @author Cristian Harja
 */
public class LdfParserTest extends AbstractParserTest{

    @Test
    public void testParse1() throws Exception {
        LdfParser parser = initParser("example1.txt");
        assertNull(parser.getParseError());
        Object result = parser.getAbstractSyntaxTree();
        System.out.println(result.getClass().getName());
    }

    @Test
    public void testParse2() throws Exception {
        LdfParser parser = initParser("example2.txt");
        assertNull(parser.getParseError());
        Object result = parser.getAbstractSyntaxTree();
        System.out.println(result.getClass().getName());
    }

    @Test
    public void testAnalysis1() throws Exception {
        LdfParser parser;
        long start, stop;

        start = System.currentTimeMillis();

        parser = initParser("example3.txt");
        assertNull(parser.getParseError());
        parser.syntaxCheck();

        stop = System.currentTimeMillis();

        for(Result r: parser.getResults()) {
            System.out.println(r);
        }

        System.out.println(
                "Completed in " + (stop - start) + " milliseconds"
        );
    }

}
