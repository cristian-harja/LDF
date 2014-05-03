package ldf.compiler.phases;

import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.context.CompilerContext;
import ldf.compiler.semantics.symbols.Scope;

import javax.annotation.Nonnull;

/**
 * Walks recursively through the AST of each source file and configures a
 * {@link Scope} object.
 *
 * @author Cristian Harja
 */
public final class Phase_InitScopes {

    private Phase_InitScopes() {
    }

    public static void initScopes(
            @Nonnull CompilerContext ctx,
            @Nonnull AstSourceFile file
    ) {
        Scope globalScope = ctx.getGlobalScope();
        file.setScope(globalScope.fork());

        initChildrenScopes(file);
    }

    private static void initChildrenScopes(@Nonnull AstNode node) {
        Scope scope = node.getScope();
        if (scope == null) {
            throw new IllegalArgumentException(
                    "Scope is not initialized for given node"
            );
        }
        for (AstNode n : node) {
            n.setScope(n.hasOwnScope() ? scope.fork() : scope);
            initChildrenScopes(n);
        }
    }

}
