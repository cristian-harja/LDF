package ldf.parser.ast.expr;

import ldf.parser.ast.stmt.StmtBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Lambda expressions. A lambda expression is a function definition used as
 * value (first-class citizen).
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprLambda implements Expression {

    @Nonnull
    private FormalParamList params;

    private Expression expr;

    private StmtBlock code;

    public ExprLambda(
            @Nonnull FormalParamList params,
            @Nonnull StmtBlock code
    ) {
        this.params = params;
        this.code = code;
        this.expr = null;
    }

    public ExprLambda(
            @Nonnull FormalParamList params,
            @Nonnull Expression expr
    ) {
        this.params = params;
        this.code = null;
        this.expr = expr;
    }


    @Nonnull
    public FormalParamList getParams() {
        return params;
    }

    @Nullable
    public Expression getExpr() {
        return expr;
    }

    @Nullable
    public StmtBlock getCode() {
        return code;
    }
}