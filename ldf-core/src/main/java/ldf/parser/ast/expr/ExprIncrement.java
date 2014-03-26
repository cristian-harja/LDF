package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * AST node for prefix/postfix increment/decrement.
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprIncrement implements Expression {
    private boolean increment;
    private boolean postfix;

    @Nonnull
    private Expression target;

    /**
     * @param increment whether this is an increment (++) or decrement (--)
     * @param postfix whether it is postfix (a++) or prefix (++a)
     * @param target the variable being updated
     */
    public ExprIncrement(
            boolean increment,
            boolean postfix,
            @Nonnull Expression target
    ) {
        this.increment = increment;
        this.postfix = postfix;
        this.target = target;
    }

    public boolean isIncrement() {
        return increment;
    }

    public boolean isPostfix() {
        return postfix;
    }

    @Nonnull
    public Expression getTarget() {
        return target;
    }
}
