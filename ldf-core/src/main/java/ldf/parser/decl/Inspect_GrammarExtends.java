package ldf.parser.decl;

import ldf.parser.Context;
import ldf.parser.Util;
import ldf.parser.ast.AstNode;
import ldf.parser.ast.AstSourceFile;
import ldf.parser.ast.Reference;
import ldf.parser.ast.decl.DeclGrammar;
import ldf.parser.ast.decl.DeclNonTerminal;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;
import ldf.parser.util.Predicate;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Cristian Harja
 */
public class Inspect_GrammarExtends
        extends Inspection<Context, DeclGrammar>{

    @Override
    protected boolean filter(Context ctx, @Nonnull DeclGrammar obj) {
        return !obj.getExtendedGrammars().isEmpty();
    }

    @Override
    protected boolean inspect(Context ctx, @Nonnull DeclGrammar obj) {
        Set<DeclGrammar> extended = new TreeSet<DeclGrammar>(
                Util.NATIVE_HASH_COMPARATOR
        );

        for (Reference ref: obj.getExtendedGrammars()) {
            AstSourceFile root = obj.getParentOfType(AstSourceFile.class);
            DeclGrammar g = root.getGrammar(ref.getPath().get(0).getName());
            if (g == null) {
                Result res = new Result();
                res.fileName = ctx.getFilename();
                res.pos = ref;
                res.type = Result.Type.ERROR;
                res.msg = "Grammar not found";
                ctx.report(res);
                continue;
            }
            if (extended.contains(g)) {
                Result res = new Result();
                res.fileName = ctx.getFilename();
                res.pos = ref;
                res.type = Result.Type.ERROR;
                res.msg = "Grammar inherited twice";
                ctx.report(res);
                continue;
            }
            extended.add(g);
            Iterator<AstNode> it = obj.findAllByDFS(
                    new Predicate<AstNode>() {
                        @Override
                        public boolean eval(AstNode target) {
                            return target instanceof DeclNonTerminal;
                        }
                    }
            );
            while (it.hasNext()) {
                DeclNonTerminal nterm = (DeclNonTerminal) it.next();
                nterm.getOwnScope().importScope(g.getOwnScope());
            }
            /*
            obj.getNearestScope().referenceSymbol(
                    ref.getPath().get(0),
                    SymbolType.GRAMMAR.bitMask
            );
            */
        }
        return true;
    }

    private Inspect_GrammarExtends() {
        super(DeclGrammar.class);
    }

    private static final Inspect_GrammarExtends
            instance = new Inspect_GrammarExtends();

    public static Inspect_GrammarExtends getInstance() {
        return instance;
    }
}
