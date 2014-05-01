package ldf.compiler.ast.stmt;

import ldf.compiler.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * {@code while} statement. Syntactic sugar: {@code while(...) {...} else
 * {...}} (the {@code else} clause is executed if the loop is never
 * entered).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtWhile extends Statement {

    private boolean    doWhile;

    @Nonnull
    private Expression exprCond;

    @Nonnull
    private Statement  stmtLoop;

    @Nullable
    private Statement  stmtElse;

    /**
     * @param doWhile whether this is a {@code do { ... } while (...) ;}
     * @param exprCond the condition in the {@code while} statement
     * @param stmtLoop the body of the loop
     * @param stmtElse optional {@code else} clause (when doWhile = false).
     */
    public StmtWhile(
            boolean doWhile,
            @Nonnull Expression exprCond,
            @Nonnull Statement stmtLoop,
            @Nullable Statement stmtElse
    ) {
        this.doWhile = doWhile;
        this.exprCond = exprCond;
        this.stmtLoop = stmtLoop;
        this.stmtElse = stmtElse;
        addAstChildren(exprCond, stmtLoop, stmtElse);
    }

    public boolean isDoWhile() {
        return doWhile;
    }

    @Nonnull
    public Expression getCondition() {
        return exprCond;
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
