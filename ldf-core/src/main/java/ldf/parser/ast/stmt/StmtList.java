package ldf.parser.ast.stmt;

import ldf.parser.Util.ListBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * A list of statements. Not a statement by itself, but it's used in a
 * couple of places. Backed by the {@code stmt_list} non-terminal.
 *
 * @author Cristian Harja
 */
@Immutable
public final class StmtList {

    @Nonnull
    private final List<Statement> items;

    private StmtList(@Nonnull List<Statement> items) {
        this.items = items;
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
