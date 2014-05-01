package ldf.compiler.ast.stmt;

import ldf.compiler.util.Util.ListBuilder;
import ldf.compiler.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * A list of statements. Not a statement by itself, but it's used in a
 * couple of places. Backed by the {@code stmt_list} non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtList extends AstNode {

    @Nonnull
    private final List<Statement> items;

    private StmtList(@Nonnull List<Statement> items) {
        this.items = items;
        addAstChildren(items);
    }

    @Nonnull
    public List<Statement> getItems() {
        return items;
    }

    /**
     * Builds {@link StmtList} objects.
     */
    @NotThreadSafe
    public static class Builder extends ListBuilder<Statement, Builder> {

        @Nonnull
        public StmtList build() {
            assertNotBuilt();
            return new StmtList(buildList());
        }
    }

}
