package ldf.parser.ast.stmt;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static ldf.parser.Util.ListBuilder;

/**
 * A list of statements, within a pair of brackets (ex: {@code { ... }} ).
 * Backed by the {@code stmt_block} non-terminal in the grammar.
 *
 * @author Cristian Harja
 */
@Immutable
public final class StmtBlock implements Statement {

    @Nonnull
    private List<Statement> statements;

    public StmtBlock(@Nonnull StmtList list) {
        this(list.getItems());
    }

    private StmtBlock(@Nonnull List<Statement> stmts) {
        statements = unmodifiableList(stmts);
    }

    @Nonnull
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * Builds {@link StmtBlock} objects.
     */
    public static class Builder extends ListBuilder<Statement, Builder>{

        @Nonnull
        public StmtBlock build() {
            assertNotBuilt();
            return new StmtBlock(buildList());
        }

    }
}
