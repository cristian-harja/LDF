package ldf.parser.ast.bnf;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.decl.Declaration;

import javax.annotation.Nonnull;

/**
 * Used by {@link BnfItem} to label {@link BnfAtom BnfAtom}s for future use
 * from within a grammar action.
 *
 * @author Cristian Harja
 */
public final class BnfLabel extends Declaration {

    @Nonnull
    private AstIdentifier id;

    public BnfLabel(@Nonnull AstIdentifier id) {
        this.id = id;
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

}
