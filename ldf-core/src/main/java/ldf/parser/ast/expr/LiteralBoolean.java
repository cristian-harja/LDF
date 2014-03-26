package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Boolean literal.
 *
 * @author Cristian Harja
 */
@Immutable
public final class LiteralBoolean implements ExprLiteral {

    @Nonnull
    private Boolean value;

    public LiteralBoolean (@Nonnull Boolean b) {
        value = b;
    }

    public LiteralBoolean(boolean b) {
        value = b;
    }

    @Nonnull
    public Boolean getValue() {
        return value;
    }
}
