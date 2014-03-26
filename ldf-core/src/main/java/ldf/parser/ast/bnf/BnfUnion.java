package ldf.parser.ast.bnf;

import ldf.parser.Util.ListBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * <p>AST node for a <em>union</em> in the BFN syntax. Syntax: {@code
 * a|b|c|...}. Backed by the {@code bnf_union} non-terminal.</p>
 *
 * <p>Concatenation has a higher precedence than union, similarly to how
 * multiplication has higher precedence than addition.</p>
 *
 * @author Cristian Harja
 */
public final class BnfUnion implements BnfAtom {
    @Nonnull
    private List<BnfConcat> items;

    /**
     * @param items items in the union
     */
    private BnfUnion(@Nonnull List<BnfConcat> items) {
        this.items = items;
    }

    /**
     * @return items in this union
     */
    @Nonnull
    public List<BnfConcat> getItems() {
        return items;
    }

    /**
     * @return {@link BnfAtomType#UNION}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.UNION;
    }

    /**
     * Builds {@link BnfUnion} objects.
     */
    @NotThreadSafe
    public static class Builder extends ListBuilder<BnfConcat, Builder>{

        public BnfUnion build() {
            assertNotBuilt();
            return new BnfUnion(buildList());
        }

    }

}
