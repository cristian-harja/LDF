package ldf.compiler;

import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.context.ContextImpl;
import ldf.compiler.context.ParserContext;
import ldf.compiler.gen.Lexer;
import ldf.compiler.gen.parser;
import ldf.compiler.inspect.InspectionSet;
import ldf.compiler.syntax.Check_BnfAbstractAction;
import ldf.compiler.syntax.Check_BnfQuantifier;
import ldf.compiler.syntax.Check_LiteralString;
import ldf.compiler.syntax.tree.LdfTokenFactory;
import ldf.compiler.syntax.tree.StNode;
import ldf.compiler.syntax.tree.StNodeFactory;
import ldf.compiler.util.StreamRecorder;
import ldf.compiler.util.SubSequenceImpl;
import ldf.java_cup.runtime.LocationAwareEntity;
import ldf.java_cup.runtime.Scanner;
import ldf.java_cup.runtime.Symbol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.SortedSet;

/**
 * @author Cristian Harja
 */
public final class LdfParser extends ContextImpl
        implements ParserContext {

    private boolean syntaxTree;
    private boolean recordInput;

    private CharSequence recordedInput;
    private Reader reader;
    private parser parser;

    private boolean parsed;
    private boolean success;

    private StNode        stRoot;
    private AstSourceFile astRoot;

    private String fileName;

    private boolean hasErrors;

    public LdfParser(LdfParserSettings settings)
            throws FileNotFoundException {
        initInputMethod(settings);
        initParser(settings);
        initLogger(settings);
    }

    private void initInputMethod(LdfParserSettings settings)
            throws FileNotFoundException {

        recordInput = settings.recordInput;

        if (settings.inputString != null) {
            reader = new StringReader(settings.inputString);
            if (recordInput) {
                recordedInput = new SubSequenceImpl(settings.inputString);
            }
        } else {
            if (settings.inputReader != null) {
                reader = settings.inputReader;
            } else if (settings.inputStream != null) {
                reader = new InputStreamReader(settings.inputStream);
            } else if (settings.inputFile != null) {
                reader = new FileReader(settings.inputFile);
            } else {
                throw new InvalidParameterException(
                        "No input method provided"
                );
            }
            if (recordInput) {
                StreamRecorder rec;
                if (settings.inputFile != null) {
                    long len = settings.inputFile.length();
                    rec = new StreamRecorder(reader,
                            (int) Math.min(len, 1024 * 1024)
                    );
                } else {
                    rec = new StreamRecorder(reader);
                }
                reader = rec;
                recordedInput = rec;
            }
        }

        if (settings.fileName != null) {
            fileName = settings.fileName;
        } else if (settings.inputFile != null) {
            fileName = settings.inputFile.getName();
        } else {
            fileName = null;
        }
    }

    private void initParser(LdfParserSettings settings) {

        syntaxTree = settings.syntaxTree;

        LdfTokenFactory symbolFactory = syntaxTree
                ? new StNodeFactory()
                : new LdfTokenFactory();

        symbolFactory.setParserContext(this);

        Scanner scanner = new Lexer(reader, symbolFactory);

        parser  = new parser(scanner, symbolFactory);

    }

    private void initLogger(LdfParserSettings settings) {
        initLogger(
                settings.locale,
                settings.i18n,
                settings.logger
        );
    }

    /**
     * If {@code recordInput} was set to {@code true}, this method
     * retrieves a portion of the input text (which was recorder directly
     * from the parser's input).
     */
    public CharSequence getRecordedText(@Nonnull LocationAwareEntity loc) {
        return recordInput ? recordedInput.subSequence(
                loc.getOffsetL(), loc.getOffsetR()
        ) : null;
    }

    /**
     * Runs the LALR(1) parser (which backs the LDF parser).
     */
    public synchronized void parseInput() {
        if (parsed) {
            return;
        }
        try {
            Symbol parseResult;
            parseResult = parser.parse(); // invoke the parser
            astRoot = (AstSourceFile) parseResult.value;
            if (syntaxTree) {
                stRoot = (StNode) parseResult;
            }
            if (parser.failed) {
                reportError(
                        fileName, parser.unrecoveredErrorPosition,
                        i18n().getString("syntax.unrecovered_error")
                );
            }
            success = !parser.failed;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parsed = true;
        }
    }

    /**
     * If the parser completed successfully, returns the root AST node.
     */
    public AstSourceFile getAbstractSyntaxTree() {
        parseInput();
        return astRoot;
    }

    /**
     * If the parser completed successfully, returns the root ST node.
     */
    public StNode getSyntaxTree() {
        parseInput();
        return stRoot;
    }

    /**
     * Runs the LALR(1) parser and runs a couple of inspections over the
     * AST nodes.
     */
    public void syntaxCheck() {
        parseInput();
        if (!success) {
            return;
        }

        @SuppressWarnings("ALL")
        InspectionSet<ParserContext, AstNode>
                inspections = new InspectionSet<ParserContext, AstNode>();

        inspections.addAll(Arrays.asList(
                Check_BnfQuantifier.getInstance(),
                Check_BnfAbstractAction.getInstance(),
                Check_LiteralString.getInstance()
        ));

        inspections.runAllOnIterator(this, astRoot.findAllByDFS());

    }

    /**
     * @return a list of errors/warnings, sorted by their position in the
     *         input
     */
    public SortedSet<CompilerLog.Entry> getResults() {
        return getLogger().getMessages();
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public void reportError(
            @Nullable LocationAwareEntity pos,
            @Nonnull String format, Object... args
    ) {
        reportError(getFileName(), pos, format, args);
    }

    @Override
    public void reportWarn(
            @Nullable LocationAwareEntity pos,
            @Nonnull String format, Object... args
    ) {
        reportWarn(getFileName(), pos, format, args);
    }

    public boolean successful() {
        return success;
    }
}
