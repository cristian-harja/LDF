package ldf.parser.ast;

import ldf.parser.ast.decl.DeclGrammar;
import ldf.parser.ast.decl.DeclList;
import ldf.parser.ast.decl.Declaration;

import javax.annotation.Nonnull;

import static ldf.parser.decl.SymbolType.FUNCTION;
import static ldf.parser.decl.SymbolType.GRAMMAR;

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

    @Override
    protected int getAcceptedTypes() {
        return GRAMMAR.bitMask | FUNCTION.bitMask;
    }

    public DeclGrammar getGrammar(String name) {
        for (Declaration decl: getDeclarations().getItems()) {
            if (decl.getDeclaredSymbolName().getName().equals(name) &&
                decl.getDeclaredSymbolType() == GRAMMAR
            ) {
                return (DeclGrammar) decl;
            }
        }
        return null;
    }
}
