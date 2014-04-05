package ldf.parser;

import java.io.*;

/**
 * Provides the means for setting up an instance of the {@link LdfParser}.
 *
 * @author Cristian Harja
 */
public class LdfParserSettings {

    String      fileName;
    Reader      inputReader;
    InputStream inputStream;
    File        inputFile  ;
    String      inputString;

    boolean syntaxTree;
    boolean recordInput;


    public void setInput(Reader reader) {
        inputReader = reader;
        inputStream = null;
        inputFile   = null;
        inputString = null;
    }

    public void setInput(InputStream in) {
        inputReader = null;
        inputStream = in;
        inputFile   = null;
        inputString = null;
    }

    public void setInput(File file) {
        inputReader = null;
        inputStream = null;
        inputFile   = file;
        inputString = null;
    }

    public void setInput(String string) {
        inputReader = null;
        inputStream = null;
        inputFile   = null;
        inputString = string;
    }

    public void overrideFileName(String name) {
        fileName = name;
    }

    public void setUseSyntaxTree(boolean b) {
        syntaxTree = b;
    }

    public void setRecordInput(boolean b) {
        recordInput = b;
    }


}
