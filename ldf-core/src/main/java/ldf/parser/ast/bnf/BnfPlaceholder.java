package ldf.parser.ast.bnf;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * An item in the BNF syntax which expands to a grammar action (in a
 * {@code where} clause).
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfPlaceholder implements BnfAbstractAction {

    @Nonnull
    private String label;

    /**
     * @param label the name of this placeholder
     */
    public BnfPlaceholder(@Nonnull String label) {
        this.label = label;
    }

    /**
     * @return name of this placeholder
     */
    @Nonnull
    public String getLabel() {
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
