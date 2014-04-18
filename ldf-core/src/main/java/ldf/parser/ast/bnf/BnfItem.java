package ldf.parser.ast.bnf;

import ldf.parser.ags.AgsNode;
import ldf.parser.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

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
@ThreadSafe
public final class BnfItem extends AstNode
        implements BnfAtom {

    @Nullable
    private BnfLabel label;

    @Nonnull
    private BnfAtom atom;

    @Nullable
    private BnfQuantifier quantifier;

    @Nonnull
    private AgsNode agsNode;

    /**
     * @param label optional label
     * @param atom kernel
     * @param quantifier optional quantifier
     */
    public BnfItem(
            @Nullable BnfLabel label,
            @Nonnull BnfAtom atom,
            @Nullable BnfQuantifier quantifier
    ) {
        this.label = label;
        this.atom = atom;
        this.quantifier = quantifier;
        addAstChildren(label, (AstNode) atom, quantifier);
        agsNode = AgsNode.agsInit(this);
    }

    /**
     * @return the label associated with this item (or {@literal null} if
     *         there isn't one).
     */
    @Nullable
    public BnfLabel getLabel() {
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

    @Nonnull
    @Override
    public AgsNode getAbstractGrammarSpec() {
        return agsNode;
    }
}
