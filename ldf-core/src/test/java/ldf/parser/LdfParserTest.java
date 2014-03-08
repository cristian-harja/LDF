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
        assertNull(parser.getParseError());
    }

    @Test
    public void testParse2() throws Exception {
        LdfParser parser = new LdfParser(
            getClass().getResourceAsStream("example2.txt")
        );
        assertNull(parser.getParseError());
    }
}
