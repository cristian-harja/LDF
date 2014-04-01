package ldf.parser.ast.bnf;

import ldf.parser.Util.ListBuilder;
import ldf.parser.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
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
@ThreadSafe
public final class BnfUnion extends AstNode
        implements BnfAtom
{
    @Nonnull
    private List<BnfConcat> items;

    /**
     * @param items items in the union
     */
    private BnfUnion(@Nonnull List<BnfConcat> items) {
        this.items = items;
        addAstChildren(items);
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
