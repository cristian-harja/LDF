package ldf.compiler.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Boolean literal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class LiteralBoolean extends ExprLiteral {

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
