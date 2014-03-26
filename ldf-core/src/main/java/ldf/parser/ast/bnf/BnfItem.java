package ldf.parser.ast.bnf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * <p>AST node for BNF <em>items</em>, which are the building blocks of BNF
 * expressions (they are combined using concatenation and union).</p>
 *
 * <p>An item is esentially an {@link BnfAtom} with an optional label
 * ({@link String}) and an optional {@link BnfQuantifier} (which indicates
 * the desired number of repetitions of the item).</p>
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfItem implements BnfAtom {

    @Nullable
    private String label;

    @Nonnull
    private BnfAtom atom;

    @Nullable
    private BnfQuantifier quantifier;

    /**
     * @param label optional label
     * @param atom kernel
     * @param quantifier optional quantifier
     */
    public BnfItem(
            @Nullable String label,
            @Nonnull BnfAtom atom,
            @Nullable BnfQuantifier quantifier
    ) {
        this.label = label;
        this.atom = atom;
        this.quantifier = quantifier;
    }

    /**
     * @return the label associated with this item (or {@literal null} if
     *         there isn't one).
     */
    @Nullable
    public String getLabel() {
        return label;
    }

    /**
     * @return the kernel of this item
     */
    @Nonnull
    public BnfAtom getAtom() {
        return atom;
    }

    /**
     * @return the quantifier associated with this item (or {@literal null}
     *         if there isn't any).
     */
    @Nullable
    public BnfQuantifier getQuantifier() {
        return quantifier;
    }

    /**
     * @return {@link BnfAtomType#ITEM}
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.ITEM;
    }
}
