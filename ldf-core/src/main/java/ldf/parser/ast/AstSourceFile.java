package ldf.parser.ast;

import ldf.parser.ast.decl.DeclList;

import javax.annotation.Nonnull;

/**
 * Root of the AST; represents a source file.
 *
 * @author Cristian Harja
 */
public final class AstSourceFile extends AstNode {
    private DeclList declarations;

    public AstSourceFile(@Nonnull DeclList declarations) {
        this.declarations = declarations;
        addAstChildren(declarations);
    }

    public DeclList getDeclarations() {
        return declarations;
    }

    @Override
    public boolean hasOwnScope() {
        return true;
    }
}
