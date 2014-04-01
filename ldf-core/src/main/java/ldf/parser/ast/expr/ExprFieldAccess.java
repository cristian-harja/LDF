package ldf.parser.ast.expr;

import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Field access (ex: {@code a.b}).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class ExprFieldAccess extends Expression {

    @Nonnull
    private Expression object;

    @Nonnull
    private AstIdentifier field;

    public ExprFieldAccess(
            @Nonnull Expression object,
            @Nonnull AstIdentifier field
    ) {
        this.object = object;
        this.field = field;
        addAstChildren(object, field);
    }

    @Nonnull
    public Expression getObject() {
        return object;
    }

    @Nonnull
    public AstIdentifier getField() {
        return field;
    }
}
