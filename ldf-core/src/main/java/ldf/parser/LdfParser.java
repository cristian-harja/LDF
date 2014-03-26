package ldf.parser;

import ldf.java_cup.runtime.*;
import ldf.parser.gen.Lexer;
import ldf.parser.gen.parser;

import java.io.*;

/**
 * @author Cristian Harja
 */
@SuppressWarnings("unused")
public class LdfParser {

    private Reader _input;

    private boolean createSyntaxTree;

    private boolean parsed;
    private Symbol parseResult;
    private Exception parseError;

    public LdfParser(Reader isr) {
        _input = isr;
    }

    public LdfParser(InputStream in) {
        this(new InputStreamReader(in));
    }

    public LdfParser(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public LdfParser(String inputString) {
        this(new StringReader(inputString));
    }

    protected void parse() {
        if (parsed) {
            return;
        }
        try {
            ldf.java_cup.runtime.TokenFactory tf;
            tf = createSyntaxTree
                    ? new TokenFactory()
                    : new TokenFactoryImpl()
            ;
            Scanner s = new Lexer(_input, tf);
            parser  p = new parser(s, tf);
            parseResult = p.parse();
        } catch (Exception e) {
            e.printStackTrace();
            parseError = e;
        } finally {
            parsed = true;
        }
    }

    public Exception getParseError() {
        parse();
        return parseError;
    }

    public Object getParseResult() {
        parse();
        return parseResult != null ? parseResult.value : null;
    }

    public StNode getSyntaxTree() {
        parse();
        return createSyntaxTree ? (StNode) parseResult : null;
    }

    public void setCreateSyntaxTree(boolean createSyntaxTree) {
        if (parsed) {
            throw new IllegalStateException(
                    "Input already parsed"
            );
        }
        this.createSyntaxTree = createSyntaxTree;
    }
}
