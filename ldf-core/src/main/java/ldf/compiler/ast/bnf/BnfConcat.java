package ldf.compiler.ast.bnf;

import ldf.compiler.ags.AgsNode;
import ldf.compiler.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static ldf.compiler.util.Util.ListBuilder;

/**
 * <p>AST node for <em>concatenations</em> in the BFN syntax. Syntax:
 * {@code a b c ...}. Backed by the {@code bnf_concatenation} non-terminal.
 * </p>
 * <p>Concatenation has a higher precedence than union (the {@code |}
 * operator), similarly to how multiplication has higher precedence than
 * addition.</p>
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class BnfConcat extends AstNode
        implements BnfAtom {

    @Nonnull
    private List<BnfItem> items;

    @Nonnull
    private AgsNode agsNode;

    /**
     * @param items elements being concatenated
     */
    private BnfConcat(@Nonnull List<BnfItem> items) {
        this.items = items;
        addAstChildren(items);
        agsNode = AgsNode.agsInit(this);
    }

    @Nonnull
    public List<BnfItem> getItems() {
        return items;
    }

    /**
     * @return {@link BnfAtomType#CONCATENATION}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.CONCATENATION;
    }

    @Nonnull
    @Override
    public AgsNode getAbstractGrammarSpec() {
        return agsNode;
    }

    /**
     * Builds {@link BnfConcat} objects.
     */
    public static class Builder extends ListBuilder<BnfItem, Builder> {

        public BnfConcat build() {
            assertNotBuilt();
            return new BnfConcat(buildList());
        }

    }

}
