package ldf.parser.ast.stmt;

import ldf.parser.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * An expression treated as a statement. The outcome of the statement is
 * the side effects of evaluating the expression (eg.function calls,
 * assignments).Backed by the {@code stmt_expr} non-terminal.</p>
 *
 * @author Cristian Harja
 */
@Immutable
public final class StmtExpression implements Statement {

    @Nonnull
    private Expression e;

    public StmtExpression(@Nonnull Expression e) {
        this.e = e;
    }

    @Nonnull
    public Expression getExpression() {
        return e;
    }
}
