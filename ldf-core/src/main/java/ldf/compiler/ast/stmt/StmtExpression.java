package ldf.compiler.ast.stmt;

import ldf.compiler.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * An expression treated as a statement. The outcome of the statement is
 * the side effects of evaluating the expression (eg.function calls,
 * assignments).Backed by the {@code stmt_expr} non-terminal.</p>
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtExpression extends Statement {

    @Nonnull
    private Expression e;

    public StmtExpression(@Nonnull Expression e) {
        this.e = e;
        addAstChildren(e);
    }

    @Nonnull
    public Expression getExpression() {
        return e;
    }
}
