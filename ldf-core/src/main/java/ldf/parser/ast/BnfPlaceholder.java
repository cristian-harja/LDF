package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * @author Cristian Harja
 */
@Immutable
public final class BnfPlaceholder extends BnfAbstractAction {

    @Nonnull
    private String label;

    public BnfPlaceholder(@Nonnull String label) {
        this.label = label;
    }

    @Nonnull
    public String getLabel() {
        return label;
    }

    @Override
    public Type getType() {
        return Type.PLACEHOLDER;
    }

}
