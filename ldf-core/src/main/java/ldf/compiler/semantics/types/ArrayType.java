package ldf.compiler.semantics.types;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;

/**
 * @author Cristian Harja
 */
@Immutable
public class ArrayType extends DataType {

    @Nonnull
    private DataType baseType;

    public ArrayType(@Nonnull DataType type) {
        baseType = type;
    }

    @Override
    public final boolean isArray() {
        return true;
    }

    @Override
    protected boolean isAssignableFromImpl(DataType t) {
        return t instanceof ArrayType &&
                ((ArrayType)t).getBaseType().equals(baseType);
    }

    @Nonnull
    public DataType getBaseType() {
        return baseType;
    }

    @Override
    public void format(@Nonnull Appendable out) throws IOException {
        out.append('[');
        baseType.format(out);
        out.append(']');
    }

    @Override
    protected boolean equals(@Nonnull DataType t) {
        return (t instanceof ArrayType) && baseType.equals(
                ((ArrayType) t).baseType
        );
    }
}
