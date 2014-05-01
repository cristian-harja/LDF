package ldf.compiler.ast.stmt;

import ldf.compiler.ast.expr.ExprList;
import ldf.compiler.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>{@code for} statement (as seen in languages like Java, C, C++, etc).
 * Backed by the {@code stmt_for} non-terminal.</p>
 *
 * <p><b>Issue</b>: the initializer statement must be an expression (can't
 * declare new variables like so: {@code for (var i = 0; ...; ...) ... }.
 * <b>Subject to change</b>.</p>
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtFor extends Statement {

    @Nullable
    private ExprList exprInit;

    @Nullable
    private Expression exprCond;

    @Nullable
    private Expression exprNext;

    @Nonnull
    private Statement  stmtLoop;

    @Nullable
    private Statement  stmtElse;

    public StmtFor(
            @Nullable ExprList   exprInit,
            @Nullable Expression exprCond,
            @Nullable Expression exprNext,
            @Nonnull  Statement  stmtLoop,
            @Nullable Statement  stmtElse) {
        this.exprInit = exprInit;
        this.exprCond = exprCond;
        this.exprNext = exprNext;
        this.stmtLoop = stmtLoop;
        this.stmtElse = stmtElse;
        addAstChildren(exprInit, exprCond, exprNext, stmtLoop, stmtElse);
    }

    @Nullable
    public ExprList getExprInit() {
        return exprInit;
    }

    @Nullable
    public Expression getExprCond() {
        return exprCond;
    }

    @Nullable
    public Expression getExprNext() {
        return exprNext;
    }

    @Nonnull
    public Statement getLoopStatement() {
        return stmtLoop;
    }

    @Nullable
    public Statement getElseStatement() {
        return stmtElse;
    }
}
