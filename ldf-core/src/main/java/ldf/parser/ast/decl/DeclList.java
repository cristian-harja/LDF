package ldf.parser.ast.decl;

import ldf.parser.Util.ListBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * A list of declarations. Not a declaration by itself, but used in places
 * where only a list declarations is accepted (ex: the contents of a
 * class). Backed by the {@code decl_list} non-terminal.
 *
 * @author Cristian Harja
 */
@Immutable
public final class DeclList {

    @Nonnull
    private List<Declaration> items;

    private DeclList(@Nonnull List<Declaration> items) {
        this.items = items;
    }

    @Nonnull
    public List<Declaration> getItems() {
        return items;
    }

    /**
     * Builds {@link DeclList} objects.
     */
    @NotThreadSafe
    public static class Builder extends ListBuilder<Declaration, Builder> {
        public DeclList build() {
            assertNotBuilt();
            return new DeclList(buildList());
        }

    }

}
