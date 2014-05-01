package ldf.compiler.ast.stmt;

import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.expr.Expression;
import ldf.compiler.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static ldf.compiler.util.Util.ListBuilder;

/**
 * {@code switch} statement. Has features which help detect syntax errors.
 *
 * @author Cristian Harja
 */
public final class StmtSwitch extends Statement {

    @Nonnull
    private Expression match;

    @Nonnull
    private List<Case> cases;

    @Nonnull
    private List<Statement> noCase;

    private StmtSwitch(
            @Nonnull Expression match,
            @Nonnull List<Statement> noCase,
            @Nonnull List<Case> cases
    ) {
        this.match = match;
        this.noCase = noCase;
        this.cases = cases;
        addAstChildren(match);
        addAstChildren(noCase);
        addAstChildren(cases);
    }

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
    public static final class Case extends AstNode {

        @Nullable
        private Expression  match;

        @Nonnull
        private List<Statement> stmts;

        private Case(
                @Nullable Expression match,
                @Nonnull List<Statement> stmts
        ) {
            this.match = match;
            this.stmts = stmts;
            addAstChildren(match);
            addAstChildren(stmts);
        }

        @Nullable
        public Expression getMatch() {
            return match;
        }

        @Nullable
        public List<Statement> getStatements() {
            return stmts;
        }

        static class Builder extends ListBuilder<Statement, Builder>{

            private final Expression match;

            public Builder(Expression match) {

                this.match = match;
            }

            public Case build() {
                assertNotBuilt();
                return new Case(match, buildList());
            }
        }

    }

    /**
     * Builds {@link StmtSwitch} objects.
     */
    @NotThreadSafe
    public static class Builder {

        private Case.Builder currentCase;
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
            currentCase = new Case.Builder(e);

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
            built = true;

            return new StmtSwitch(
                    expr,
                    noCaseStmts,
                    cases
            );
        }

        private void flush() {
            if (currentCase == null) {
                if (!currentStmts.isEmpty()) {
                    noCaseStmts = unmodifiableList(currentStmts);
                    currentStmts = new ArrayList<Statement>();
                }
            } else {
                currentCase.addAll(currentStmts);
                currentStmts = new ArrayList<Statement>();
                cases.add(currentCase.build());
            }
            currentCase = null;
        }

    }

}
