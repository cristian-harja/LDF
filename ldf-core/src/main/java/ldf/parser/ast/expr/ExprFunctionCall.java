package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Function call. Basically, any expression followed by a list of
 * arguments can be treated as a fuction (as long as it evaluates to one).
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprFunctionCall implements Expression {

    @Nonnull
    private final Expression function;

    @Nonnull
    private final ExprList params;

    /**
     * @param function An expression that evaluates to a function. It can
     *                 be simply a function name or a field access.
     * @param params A list of expressions that evaluate to the actual
     *               arguments of the function.
     */
    public ExprFunctionCall (
            @Nonnull Expression function,
            @Nonnull ExprList params
    ) {
        this.function = function;
        this.params = params;
    }

    @Nonnull
    public Expression getFunction() {
        return function;
    }

    @Nonnull
    public ExprList getParams() {
        return params;
    }
}
