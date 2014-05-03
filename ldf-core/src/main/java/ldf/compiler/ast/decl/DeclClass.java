package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nonnull;

/**
 * <p>A {@code class} declaration (not in the sense of OOP).
 * </p>
 * <p>Right now it's aimed at representing AST nodes, can only be defined
 * within a grammar and can only contain variable declarations (fields).
 * </p>
 *
 * @author Cristian Harja
 */
public final class DeclClass extends Declaration {

    @Nonnull
    private AstIdentifier id;

    @Nonnull
    private DeclList declList;

    public DeclClass(
            @Nonnull AstIdentifier id,
            @Nonnull DeclList declList
    ) {
        this.id = id;
        this.declList = declList;
        addAstChildren(declList);
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    public DeclList getDeclarations() {
        return declList;
    }

    @Override
    public boolean hasOwnScope() {
        return true;
    }

    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    @Override
    public NsNodeType getDeclaredSymbolType() {
        return NsNodeType.CLASS;
    }
}
