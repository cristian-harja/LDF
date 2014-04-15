package ldf.parser.ast.expr;

import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nonnull;

/**
 * An expression containing only an identifier.
 *
 * @author Cristian Harja
 */
public final class ExprIdentifier extends Expression {
    private AstIdentifier id;

    public ExprIdentifier(@Nonnull AstIdentifier id) {
        this.id = id;
    }

    public AstIdentifier getId() {
        return id;
    }
}
