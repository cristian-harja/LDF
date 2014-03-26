package ldf.parser.ast.bnf;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;

import static ldf.parser.Util.ListBuilder;

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
@Immutable
public final class BnfConcat implements BnfAtom {

    @Nonnull
    private List<BnfItem> items;

    /**
     * @param items elements being concatenated
     */
    private BnfConcat(@Nonnull List<BnfItem> items) {
        this.items = items;
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
