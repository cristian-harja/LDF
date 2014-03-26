package ldf.parser.ast.stmt;

import ldf.parser.Util;
import ldf.parser.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * {@code switch} statement. Has features which help detect syntax errors.
 *
 * @author Cristian Harja
 */
@Immutable
public final class StmtSwitch implements Statement {

    @Nonnull
    private Expression match;

    @Nonnull
    private List<Case> cases;

    @Nonnull
    private List<Statement> noCase;

    @Nonnull
    public Expression getMatch() {
        return match;
    }

    @Nonnull
    public List<Case> getCases() {
        return cases;
    }

    @Nonnull
    public List<Statement> getNoCase() {
        return noCase;
    }

    /**
     * {@code case ...: ...} (or {@code default: ...} ).
     */
    @Immutable
    public static final class Case {

        @Nullable
        private Expression  match;

        @Nonnull
        private List<Statement> stmts;

        @Nullable
        public Expression getMatch() {
            return match;
        }

        @Nullable
        public List<Statement> getStatements() {
            return stmts;
        }
    }

    /**
     * Builds {@link StmtSwitch} objects.
     */
    @NotThreadSafe
    public static class Builder {

        private Case currentCase;
        private List<Case> cases = new LinkedList<Case>();

        private List<Statement> noCaseStmts;
        private List<Statement> currentStmts = new ArrayList<Statement>();
        private boolean built;

        public Builder beginDefaultCase() {
            return beginCase(null);
        }

        public Builder beginCase(@Nullable Expression e) {
            Util.assertNotBuilt(built, StmtSwitch.class);
            flush();
            currentCase = new Case();
            currentCase.match = e;
            cases.add(currentCase);

            return this;
        }

        public Builder addStatement(@Nonnull Statement s) {
            Util.assertNotBuilt(built, StmtSwitch.class);
            currentStmts.add(s);
            return this;
        }

        public StmtSwitch build(Expression expr) {
            Util.assertNotBuilt(built, StmtSwitch.class);
            flush();
            StmtSwitch sw = new StmtSwitch();
            sw.match = expr;
            sw.noCase = noCaseStmts;
            sw.cases = cases;

            built = true;
            return sw;
        }

        private void flush() {
            if (currentCase == null) {
                if (!currentStmts.isEmpty()) {
                    noCaseStmts = unmodifiableList(currentStmts);
                    currentStmts = new ArrayList<Statement>();
                }
            } else {
                currentCase.stmts = currentStmts;
                currentStmts = new ArrayList<Statement>();
            }
            currentCase = null;
        }

    }

}
