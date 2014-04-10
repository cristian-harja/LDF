package ldf.parser;

import ldf.java_cup.runtime.LocationAwareEntity;
import ldf.java_cup.runtime.Scanner;
import ldf.java_cup.runtime.Symbol;
import ldf.java_cup.runtime.TokenFactory;
import ldf.parser.ast.AstNode;
import ldf.parser.ast.AstSourceFile;
import ldf.parser.decl.*;
import ldf.parser.gen.Lexer;
import ldf.parser.gen.parser;
import ldf.parser.inspect.InspectionSet;
import ldf.parser.inspect.Result;
import ldf.parser.st.LdfTokenFactory;
import ldf.parser.st.StNode;
import ldf.parser.st.StNodeFactory;
import ldf.parser.syntax.Check_BnfAbstractAction;
import ldf.parser.syntax.Check_BnfQuantifier;
import ldf.parser.syntax.Check_LiteralString;
import ldf.parser.util.StreamRecorder;
import ldf.parser.util.SubSequenceImpl;

import javax.annotation.Nonnull;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Cristian Harja
 */
public final class LdfParser implements Context {

    private boolean syntaxTree;
    private boolean recordInput;

    private CharSequence recordedInput;
    private Reader reader;
    private parser parser;

    private boolean parsed;
    private boolean success;
    private Exception parseError;

    private StNode        stRoot;
    private AstSourceFile astRoot;

    private String fileName;

    private SortedSet<Result> results =
            new TreeSet<Result>(Result.COMPARATOR);

    private SortedSet<Result> readOnlyResults =
            Collections.unmodifiableSortedSet(results);

    public LdfParser(LdfParserSettings settings)
            throws FileNotFoundException {
        initInputMethod(settings);
        initParser(settings);
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
            fileName = settings.inputFile.getAbsolutePath();
        } else {
            fileName = null;
        }
    }

    private void initParser(LdfParserSettings settings) {

        syntaxTree = settings.syntaxTree;

        TokenFactory symbolFactory = syntaxTree
                ? new StNodeFactory()
                : new LdfTokenFactory();

        Scanner scanner = new Lexer(reader, symbolFactory);

        parser  = new parser(scanner, symbolFactory);

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
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            parseError = e;
        } finally {
            parsed = true;
        }
    }

    /**
     * Currently, the LALR(1) parser throws an exception when the input
     * can't be parsed. This should be fixed soon.
     */
    public Exception getParseError() {
        parseInput();
        return parseError;
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
        InspectionSet<Context, AstNode>
                inspections = new InspectionSet<Context, AstNode>();

        inspections.addAll(Arrays.asList(
                Check_BnfQuantifier.getInstance(),
                Check_BnfAbstractAction.getInstance(),
                Check_LiteralString.getInstance()
        ));

        inspections.runAllOnIterator(this, astRoot.findAllByDFS());

    }

    public void scopeCheck() {
        syntaxCheck();
        if (!success) {
            return;
        }
        astRoot.initScopes(); // recursive, top-down

        @SuppressWarnings("ALL")
        InspectionSet<Object, AstNode>
                inspectionSet1 = new InspectionSet<Object, AstNode>();

        inspectionSet1.addAll(Arrays.asList(
                Inspect_Declaration.getInstance(),
                Inspect_ExprIdentifier.getInstance(),
                Inspect_BnfItem_Reference.getInstance(),
                Inspect_BnfItem_Placeholder.getInstance(),
                Inspect_BnfSyntax.getInstance()
        ));

        inspectionSet1.runAllOnIterator(null, astRoot.findAllByDFS());

        @SuppressWarnings("ALL")
        InspectionSet<Context, AstNode>
                inspectionSet1b = new InspectionSet<Context, AstNode>();

        inspectionSet1b.add(Inspect_GrammarExtends.getInstance());

        inspectionSet1b.runAllOnIterator(this, astRoot.findAllByDFS());

        astRoot.getOwnScope().resolveReferences();

        @SuppressWarnings("ALL")
        InspectionSet<Object, AstNode>
                inspectionSet2 = new InspectionSet<Object, AstNode>();

        inspectionSet2.addAll(Arrays.asList(
                Inspect_BnfItem_Placeholder2.getInstance()
        ));

        inspectionSet2.runAllOnIterator(null, astRoot.findAllByDFS());

        astRoot.getOwnScope().resolveReferences();

        @SuppressWarnings("ALL")
        InspectionSet<Context, AstNode>
                inspectionSet3 = new InspectionSet<Context, AstNode>();

        inspectionSet3.add(
                Inspect_ShadowedParameter.getInstance()
        );

        inspectionSet3.runAllOnIterator(this, astRoot.findAllByDFS());

        @SuppressWarnings("ALL")
        InspectionSet<Context, Scope>
                inspectionSet4 = new InspectionSet<Context, Scope>();

        inspectionSet4.addAll(Arrays.asList(
                Inspect_SymbolUsage.getInstance(),
                Inspect_DuplicateDeclaration.getInstance()
        ));

        inspectionSet4.runAllOnIterator(this,
                astRoot.getOwnScope().iterateAll()
        );


    }

    @Override
    public void report(@Nonnull Result result) {
        results.add(result);
    }

    /**
     * @return a list of errors/warnings, sorted by their position in the
     *         input
     */
    public SortedSet<Result> getResults() {
        return readOnlyResults;
    }

    @Override
    public String getFilename() {
        return fileName;
    }

}
