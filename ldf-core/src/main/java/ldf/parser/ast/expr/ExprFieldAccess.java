package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Field access (ex: {@code a.b}).
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprFieldAccess implements Expression {

    @Nonnull
    private Expression object;

    @Nonnull
    private String field;

    public ExprFieldAccess(
            @Nonnull Expression object,
            @Nonnull String field
    ) {
        this.object = object;
        this.field = field;
    }

    @Nonnull
    public Expression getObject() {
        return object;
    }

    @Nonnull
    public String getField() {
        return field;
    }
}
