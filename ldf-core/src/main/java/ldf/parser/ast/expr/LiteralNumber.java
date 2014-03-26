package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Number literal (ex: {@code 12.7}, {@code -3}, etc).
 *
 * @author Cristian Harja
 */
@Immutable
public final class LiteralNumber implements ExprLiteral {
    @Nonnull
    private Number value;

    public LiteralNumber(@Nonnull Number n) {
        value = n;
    }

    @Nonnull
    public Number getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
