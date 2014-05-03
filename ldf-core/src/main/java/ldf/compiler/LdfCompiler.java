package ldf.compiler;

import ldf.compiler.context.CompilerContext;
import ldf.compiler.context.ContextImpl;
import ldf.compiler.phases.*;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.Scope;
import ldf.compiler.semantics.types.TypeEnv;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Cristian Harja
 */
@NotThreadSafe
public final class LdfCompiler extends ContextImpl
        implements CompilerContext {

    final List<File> sources;
    final TypeEnv typeEnv;
    final NsNode globalNS;
    final Scope globalScope;

    final Map<File, LdfParser> parsedFiles;

    public LdfCompiler(LdfCompilerSettings settings) throws Exception {
        sources = settings.sources;
        typeEnv = settings.typeEnv.newInstance();
        globalNS = NsNode.initGlobalNS();
        globalScope = new Scope();

        initLogger(
                settings.locale,
                settings.i18n,
                settings.logger
        );

        parsedFiles = new TreeMap<File, LdfParser>();
    }

    public void compileParsedFiles() {

        for (LdfParser parser: parsedFiles.values()) {

            // create `Scope` objects
            Phase_InitScopes.initScopes(
                    this, parser.getAbstractSyntaxTree()
            );

            // catalog declared symbols which are visible from the global scope
            Phase_CollectDeclarations.collectSymbols(
                    this, parser.getAbstractSyntaxTree()
            );
        }

        // report clashing / duplicate declarations
        Check_DeclaredSymbols.checkSymbols(globalNS);

        // populate scopes with imported/declared symbols
        for (LdfParser parser: parsedFiles.values()) {
            Phase_ResolveImports.resolveImports(
                    this, parser.getAbstractSyntaxTree()
            );
        }

        // report cyclic dependencies in grammars
        Check_GrammarExtendsCycles.checkExtends(globalNS);

    }

    @Override
    public TypeEnv getTypeEnvironment() {
        return typeEnv;
    }

    @Override
    public NsNode getGlobalNamespace() {
        return globalNS;
    }

    @Override
    public Scope getGlobalScope() {
        return globalScope;
    }

    public synchronized void parseAllFiles() {
        for (File f : sources) {
            if (f.isFile()) {
                try {
                    parseSourceCode(f);
                } catch (FileNotFoundException ignored) {
                } catch (SecurityException ignored) {
                }
            }
        }
    }

    private LdfParser parseSourceCode(File f) throws FileNotFoundException {
        LdfParser parser;
        parser = parsedFiles.get(f);
        if (parser != null) {
            return parser;
        }
        synchronized (parsedFiles) {
            parser = parsedFiles.get(f);
            if (parser != null) {
                return parser;
            }
            LdfParserSettings params = new LdfParserSettings();
            params.setUseSyntaxTree(false);
            params.setRecordInput(false);
            params.setInput(f);
            params.setLocale(getLocale());
            params.setLogger(getLogger());
            params.setI18n(i18n());

            parser = new LdfParser(params);
            parser.syntaxCheck();
            if (parser.successful()) {
                parsedFiles.put(f, parser);
            }
            return parser;
        }
    }

}
