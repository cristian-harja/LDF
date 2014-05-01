package ldf.parser;

import ldf.CompilerLog;

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;

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
    Locale locale;
    CompilerLog logger;
    ResourceBundle i18n;


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

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setLogger(CompilerLog logger) {
        this.logger = logger;
    }

    public void setI18n(ResourceBundle i18n) {
        this.i18n = i18n;
    }
}
