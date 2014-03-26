package ldf.parser.ast.stmt;

import ldf.parser.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * {@code if} statement.
 *
 * @author Cristian Harja
 */
@Immutable
public final class StmtIf implements Statement {
    @Nonnull
    private Expression exprCond;

    @Nonnull
    private Statement  stmtThen;

    @Nullable
    private Statement  stmtElse;

    public StmtIf(
            @Nonnull Expression exprCond,
            @Nonnull Statement stmtThen,
            @Nullable Statement stmtElse
    ) {
        this.exprCond = exprCond;
        this.stmtThen = stmtThen;
        this.stmtElse = stmtElse;
    }

    @Nonnull
    public Expression getCondition() {
        return exprCond;
    }

    @Nonnull
    public Statement getThen() {
        return stmtThen;
    }

    @Nullable
    public Statement getElse() {
        return stmtElse;
    }
}
