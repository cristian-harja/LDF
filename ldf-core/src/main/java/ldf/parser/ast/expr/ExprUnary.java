package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Unary expression. There are few possible {@link UnaryOp unary
 * operators}. You can receive the one associated with this expression
 * via the {@link #getOperator()} method.
 *
 * @see UnaryOp
 * @author Cristian Harja
 */
@ThreadSafe
public final class ExprUnary extends Expression {
    @Nonnull
    private UnaryOp operator;

    @Nonnull
    private Expression target;

    public ExprUnary(
            @Nonnull UnaryOp operator,
            @Nonnull Expression target
    ) {
        this.operator = operator;
        this.target = target;
        addAstChildren(target);
    }

    @Nonnull
    public UnaryOp getOperator() {
        return operator;
    }

    @Nonnull
    public Expression getTarget() {
        return target;
    }
}
