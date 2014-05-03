package ldf.compiler.phases;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.ImportList;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.decl.DeclGrammar;
import ldf.compiler.context.CompilerContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.Scope;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Populates {@link Scope} objects with imported symbols (via the {@code
 * import} statement or from extended grammars).
 *
 * @author Cristian Harja
 */
public final class Phase_ResolveImports {

    private Phase_ResolveImports() {
    }

    public static void resolveImports(
            @Nonnull CompilerContext ctx,
            @Nonnull AstSourceFile file
    ) {
        resolveImports0(ctx, file);
        resolveGrammarExtends(file);
    }

    private static void resolveImports0(
            @Nonnull CompilerContext ctx,
            @Nonnull AstSourceFile file
    ) {
        Scope globalScope = ctx.getGlobalScope();
        Scope fileScope = file.getScope();
        fileScope.importAll(file.getPackageNsNode());

        for (ImportList.Entry e : file.getImportList().getEntries()) {
            boolean[] usedWildcard = new boolean[1];
            NsNode node = globalScope.resolveStatic(
                    e.getImportRef(), true, usedWildcard
            );
            if (node == null) {
                continue;
            }
            e.setReferencedNsNode(node);
            if (usedWildcard[0]) {
                fileScope.importAll(node);
            } else {
                // TODO: use id instead of string
                AstIdentifier id = e.getImportAs();
                fileScope.importOne(node,
                        id != null ? id.getName() : null
                );
            }
        }
    }

    private static void resolveGrammarExtends(
            AstSourceFile file
    ) {
        Iterator<DeclGrammar> it = file.findAllOfType(DeclGrammar.class);
        while (it.hasNext()) {
            DeclGrammar grammar = it.next();
            for (Reference r : grammar.getExtendedGrammars()) {
                /*
                NsNode n = r.getReferencedNsNode();
                /*/
                NsNode n = grammar.getScope().resolveStatic(
                        r, false, null
                );
                //*/
                if (n == null) continue;
                grammar.getScope().importAll(n);
            }
        }
    }


}
