package ldf.compiler;

import ldf.ContextImpl;
import ldf.compiler.phases.Check_DeclaredSymbols;
import ldf.compiler.phases.Phase_CollectDeclarations;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.types.TypeEnv;
import ldf.parser.LdfParser;
import ldf.parser.LdfParserSettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Cristian Harja
 */
public final class LdfCompiler extends ContextImpl
        implements CompilerContext {

    final List<File> sources;
    final TypeEnv typeEnv;
    final NsNode globalNS;

    final Map<File, LdfParser> parsedFiles;

    public LdfCompiler(LdfCompilerSettings settings) throws Exception {
        sources = settings.sources;
        typeEnv = settings.typeEnv.newInstance();
        globalNS = NsNode.initGlobalNS();

        initLogger(
                settings.locale,
                settings.i18n,
                settings.logger
        );

        parsedFiles = new TreeMap<File, LdfParser>();
    }

    @Override
    public TypeEnv getTypeEnvironment() {
        return typeEnv;
    }

    @Override
    public NsNode getGlobalNamespace() {
        return globalNS;
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

    public synchronized void compileParsedFiles() {
        for (LdfParser parser: parsedFiles.values()) {
            Phase_CollectDeclarations.collectSymbols(
                    parser, globalNS
            );
        }
        Check_DeclaredSymbols.checkSymbols(globalNS);
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
            parser.parseInput();
            if (parser.successful()) {
                parsedFiles.put(f, parser);
            }
            return parser;
        }
    }

}
