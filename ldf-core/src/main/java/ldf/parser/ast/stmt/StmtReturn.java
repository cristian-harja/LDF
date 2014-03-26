package ldf.parser.ast.stmt;

import ldf.parser.ast.expr.Expression;

import javax.annotation.Nullable;

/**
 * {@code return} statement.
 *
 * @author Cristian Harja
 */
public final class StmtReturn implements Statement {
    private Expression expr;

    public StmtReturn(@Nullable Expression e) {
        expr = e;
    }

    @Nullable
    public Expression getExpression() {
        return expr;
    }
}
