package ldf.parser.ast;

import ldf.parser.ast.decl.DeclGrammar;
import ldf.parser.ast.decl.DeclList;
import ldf.parser.ast.decl.Declaration;

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

    public DeclGrammar findGrammar(String name) {
        for (Declaration d : declarations.getItems()) {
            if (d instanceof DeclGrammar) {
                DeclGrammar g = (DeclGrammar) d;
                if (g.getId().getName().equals(name)) {
                    return g;
                }
            }
        }
        return null;
    }

}
