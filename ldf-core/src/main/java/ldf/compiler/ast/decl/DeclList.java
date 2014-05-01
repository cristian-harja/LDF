package ldf.compiler.ast.decl;

import ldf.compiler.util.Util.ListBuilder;
import ldf.compiler.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * A list of declarations. Not a declaration by itself, but used in places
 * where only a list declarations is accepted (ex: the contents of a
 * class). Backed by the {@code decl_list} non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclList extends AstNode {

    @Nonnull
    private List<Declaration> items;

    private DeclList(@Nonnull List<Declaration> items) {
        this.items = items;
        addAstChildren(items);
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
