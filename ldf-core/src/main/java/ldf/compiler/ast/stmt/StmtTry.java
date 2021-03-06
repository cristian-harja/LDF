package ldf.compiler.ast.stmt;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.util.Util.ListBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * {@code try-catch-finally} statement.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtTry extends Statement {

    @Nonnull
    private StmtBlock tryBlock;

    @Nullable
    private StmtBlock finallyBlock;

    @Nonnull
    private List<CatchClause> catchClauses;

    private StmtTry(
            @Nonnull StmtBlock tryBlock,
            @Nonnull List<CatchClause> catchClauses,
            @Nullable StmtBlock finallyBlock
    ) {
        this.tryBlock = tryBlock;
        this.catchClauses = catchClauses;
        this.finallyBlock = finallyBlock;
        addAstChildren(tryBlock);
        addAstChildren(catchClauses);
        addAstChildren(finallyBlock);
    }

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
    public static class CatchClause extends AstNode {
        @Nonnull
        private TypeExpression typeExpression;

        @Nonnull
        private AstIdentifier id;

        @Nonnull
        private StmtBlock code;

        public CatchClause(
                @Nonnull TypeExpression typeExpression,
                @Nonnull AstIdentifier identifier,
                @Nonnull StmtBlock code
        ) {
            this.typeExpression = typeExpression;
            this.id = identifier;
            this.code = code;
            addAstChildren(typeExpression, code);
        }

        @Nonnull
        public TypeExpression getIdType() {
            return typeExpression;
        }

        @Nonnull
        public AstIdentifier getIdName() {
            return id;
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
                @Nonnull AstIdentifier id,
                @Nonnull StmtBlock code
        ) {
            return add(new CatchClause(type, id, code));
        }

        public StmtTry build(StmtBlock tryBlock, StmtBlock finallyBlock) {
            assertNotBuilt();
            return new StmtTry(
                    tryBlock,
                    buildList(),
                    finallyBlock
            );
        }

    }

}
