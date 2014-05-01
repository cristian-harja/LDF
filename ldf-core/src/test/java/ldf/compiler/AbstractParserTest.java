package ldf.compiler;

/**
 * @author Cristian Harja
 */
public abstract class AbstractParserTest {

    private long start;

    protected void beginTest() throws InterruptedException {
        Thread.sleep(100);
        start = System.currentTimeMillis();
    }

    protected void endTest() throws InterruptedException {
        long stop = System.currentTimeMillis();
        System.out.println(
                "Completed in " + (stop - start) + " milliseconds."
        );
    }

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
