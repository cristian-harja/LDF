package ldf.parser.ast.stmt;

import ldf.parser.ast.expr.Expression;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * {@code return} statement.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtReturn extends Statement {
    private Expression expr;

    public StmtReturn(@Nullable Expression e) {
        expr = e;
        addAstChildren(e);
    }

    @Nullable
    public Expression getExpression() {
        return expr;
    }
}
