package ldf.compiler.semantics.types;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Cristian Harja
 */
public final class NoType extends DataType {

    public static final DataType INSTANCE = new NoType();

    private NoType() {}

    @Override
    public void format(@Nonnull Appendable out) throws IOException {
        out.append("<error>");
    }

    @Override
    protected boolean equals(@Nonnull DataType t) {
        return false;
    }

    @Override
    protected boolean isAssignableFromImpl(DataType t) {
        return true;
    }
}
