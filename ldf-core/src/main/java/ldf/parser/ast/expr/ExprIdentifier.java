package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * An identifier used as an expression.
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprIdentifier implements Expression {
    @Nonnull
    private String id;

    public ExprIdentifier(@Nonnull String id) {
        this.id = id;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
