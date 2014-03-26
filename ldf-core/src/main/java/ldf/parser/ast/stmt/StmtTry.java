package ldf.parser.ast.stmt;

import ldf.parser.Util.ListBuilder;
import ldf.parser.ast.TypeExpression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * {@code try-catch-finally} statement.
 *
 * @author Cristian Harja
 */
@Immutable
public final class StmtTry implements Statement {

    @Nonnull
    private StmtBlock tryBlock;

    @Nullable
    private StmtBlock finallyBlock;

    @Nonnull
    private List<CatchClause> catchClauses;

    private StmtTry() {}

    @Nonnull
    public StmtBlock getTryBlock() {
        return tryBlock;
    }

    @Nullable
    public StmtBlock getFinallyBlock() {
        return finallyBlock;
    }

    @Nonnull
    public List<CatchClause> getCatchClauses() {
        return catchClauses;
    }

    /**
     * {@code catch(...) {...} }
     */
    @Immutable
    public static class CatchClause {
        @Nonnull
        private TypeExpression typeExpression;

        @Nonnull
        private String identifier;

        @Nonnull
        private StmtBlock code;

        public CatchClause(
                @Nonnull TypeExpression typeExpression,
                @Nonnull String identifier,
                @Nonnull StmtBlock code
        ) {
            this.typeExpression = typeExpression;
            this.identifier = identifier;
            this.code = code;
        }

        @Nonnull
        public TypeExpression getIdType() {
            return typeExpression;
        }

        @Nonnull
        public String getIdName() {
            return identifier;
        }

        @Nonnull
        public StmtBlock getBody() {
            return code;
        }
    }

    /**
     * Builds {@link StmtTry} objects. Starts by constructing a list of
     * {@code catch} clauses, and then adds the {@code try} and {@code
     * finally} blocks in the {@link #build build} method.
     */
    @NotThreadSafe
    public static class Builder extends ListBuilder<CatchClause, Builder> {

        public Builder addCatch(
                @Nonnull TypeExpression type,
                @Nonnull String id,
                @Nonnull StmtBlock code
        ) {
            return add(new CatchClause(type, id, code));
        }

        public StmtTry build(StmtBlock tryBlock, StmtBlock finallyBlock) {
            assertNotBuilt();
            StmtTry stmtTry = new StmtTry();
            stmtTry.tryBlock = tryBlock;
            stmtTry.catchClauses = buildList();
            stmtTry.finallyBlock = finallyBlock;
            return stmtTry;
        }

    }

}
