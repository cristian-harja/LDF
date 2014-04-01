package ldf.parser.ast.decl;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static ldf.parser.Util.ListBuilder;

/**
 * A list of variables (ex: {@code var a, b, ...;}). Backed by the {@code
 * decl_vars} non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclVars extends Declaration {

    @Nonnull
    private List<DeclVariable> items;

    private DeclVars(
            @Nonnull List<DeclVariable> items
    ) {
        this.items = items;
        addAstChildren(items);
    }

    @Nonnull
    public List<DeclVariable> getItems() {
        return items;
    }

    /**
     * Builds {@link DeclVars} objects.
     */
    @NotThreadSafe
    public static class Builder extends ListBuilder<DeclVariable, Builder> {
        public DeclVars build() {
            assertNotBuilt();
            return new DeclVars(buildList());
        }
    }

}
