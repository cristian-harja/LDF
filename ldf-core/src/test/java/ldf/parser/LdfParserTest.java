package ldf.parser;

import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * @author Cristian Harja
 */
public class LdfParserTest {
    @Test
    public void testParse1() throws Exception {
        LdfParser parser = new LdfParser(
            getClass().getResourceAsStream("example1.txt")
        );
        parser.setCreateSyntaxTree(true);
        assertNull(parser.getParseError());
        Object result = parser.getParseResult();
        System.out.println(result.getClass().getName());
    }

    @Test
    public void testParse2() throws Exception {
        LdfParser parser = new LdfParser(
            getClass().getResourceAsStream("example2.txt")
        );
        parser.setCreateSyntaxTree(true);
        assertNull(parser.getParseError());
        Object result = parser.getParseResult();
        System.out.println(result.getClass().getName());
    }
}
