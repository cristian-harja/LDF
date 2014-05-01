package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public class DeclClass extends Declaration {

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
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    public DeclList getDeclarations() {
        return declList;
    }
}
