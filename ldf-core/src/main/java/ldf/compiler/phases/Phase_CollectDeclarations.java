package ldf.compiler.phases;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.decl.Declaration;
import ldf.compiler.context.CompilerContext;
import ldf.compiler.context.ParserContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;
import ldf.compiler.semantics.symbols.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * Scans for declared symbols (packages, classes, grammars, functions,
 * fields); one of the first compiler phases.
 *
 * @author Cristian Harja
 */
public final class Phase_CollectDeclarations {
    private Phase_CollectDeclarations() {}

    private static NsNode getPackage(
            @Nonnull NsNode root,
            @Nullable Reference ref
    ) {
        if (ref == null) {
            return root;
        }
        for (AstIdentifier id : ref.getPath()) {
            if (id.getName().equals("_")) {
                ParserContext ctx = id.getParserContext();
                ctx.reportError(id, ctx.i18n().getString(
                        "syntax.wildcard.not_allowed"
                ));
                return root;
            }
            root = root.declareChild(id, NsNodeType.PACKAGE, null);
        }
        return root;
    }

    public static void collectSymbols(
            CompilerContext ctx,
            AstSourceFile file
    ) {
        NsNode packageNode = getPackage(
                ctx.getGlobalNamespace(),
                file.getPackageName()
        );
        file.setPackageNsNode(packageNode);

        Iterator<Declaration> it;
        for (it = file.findAllOfType(Declaration.class); it.hasNext(); ) {

            Declaration decl = it.next();
            AstIdentifier id = decl.getDeclaredSymbolName();
            NsNodeType t = decl.getDeclaredSymbolType();

            if (id == null) continue;
            assert t != null;

            Declaration parentDecl = decl.getParentOfType(
                    Declaration.class
            );

            NsNode parentNsNode = parentDecl != null
                    ? parentDecl.getDeclaredNsNode()
                    : packageNode;

            assert parentNsNode != null;

            parentNsNode.declareChild(id, t, decl);

        }

        Scope globalScope = ctx.getGlobalScope();
        globalScope.importAll(ctx.getGlobalNamespace());
    }
}
