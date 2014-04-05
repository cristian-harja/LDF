package ldf.parser;

/**
 * @author Cristian Harja
 */
public abstract class AbstractParserTest {

    protected LdfParser initParser(String example) throws Exception {
        LdfParserSettings settings;


        settings = new LdfParserSettings();
        settings.setRecordInput(true);
        settings.setUseSyntaxTree(true);

        settings.setInput(
                LdfParserTest.class.getResourceAsStream(example)
        );
        settings.overrideFileName(example);

        return new LdfParser(settings);
    }
}
