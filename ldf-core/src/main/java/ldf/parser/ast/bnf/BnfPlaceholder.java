package ldf.parser.ast.bnf;

import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * An item in the BNF syntax which expands to a grammar action (in a
 * {@code where} clause).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class BnfPlaceholder extends BnfAbstractAction {

    @Nonnull
    private AstIdentifier label;

    /**
     * @param label the name of this placeholder
     */
    public BnfPlaceholder(@Nonnull AstIdentifier label) {
        this.label = label;
        addAstChildren(label);
    }

    /**
     * @return name of this placeholder
     */
    @Nonnull
    public AstIdentifier getLabel() {
        return label;
    }

    /**
     * @return {@link BnfAtomType#PLACEHOLDER}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.PLACEHOLDER;
    }
}
