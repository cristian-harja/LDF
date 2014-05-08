package ldf.compiler.semantics.types;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * @author Cristian Harja
 */
@SuppressWarnings("unused")
@Immutable
public abstract class DataType {

    private int hash;
    private WeakReference<ArrayType> refArray;
    private WeakReference<String> cacheToString;

    public boolean isArray() {
        return false;
    }

    public boolean isAggregate() {
        return false;
    }

    public final boolean isAssignableFrom(DataType t) {
        DataType nt = NoType.INSTANCE;
        return this == nt || t.equals(nt) || isAssignableFromImpl(t);
    }

    protected boolean isAssignableFromImpl(DataType t) {
        return false;
    }

    @Nonnull
    public final ArrayType getArrayType() {
        ArrayType t = refArray == null ? null : refArray.get();
        if (t == null) {
            synchronized (this) {
                t = refArray == null ? null : refArray.get();
                if (t == null) {
                    t = new ArrayType(this);
                    refArray = new WeakReference<ArrayType>(t);
                }
            }
        }
        return t;
    }

    public AggregateType getAggregateType() {
        throw new RuntimeException("Not an aggregate type: " + toString());
    }

    public final void format(@Nonnull StringBuilder out) {
        try {
            format((Appendable)out);
        } catch (IOException ignored) {}
    }

    public abstract void format(@Nonnull Appendable out)
            throws IOException;

    @Override
    public String toString() {
        String s = cacheToString == null ? null : cacheToString.get();
        if (s == null) {
            synchronized (this) {
                s = cacheToString == null ? null : cacheToString.get();
                if (s == null) {
                    StringBuilder sb = new StringBuilder();
                    format(sb);
                    s = sb.toString();
                    cacheToString = new WeakReference<String>(s);
                }
            }
        }
        return s;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = toString().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof DataType) && equals(
                (DataType)obj
        );
    }

    protected abstract boolean equals(@Nonnull DataType t);

    public DataType getLeastUpperBound(DataType t) {
        return NoType.INSTANCE;
    }

    public static DataType tryComputeLeastUpperBound(
            Collection<DataType> types
    ) {
        int numTypes = types.size();
        int numArrays = 0;
        int numObject = 0;
        int numClass = 0;
        int numForeign = 0;

        for (DataType type : types) {
            if (type.isArray()) {
                numArrays++;
            } else if (type instanceof ObjectType) {
                numObject++;
            } else if (type instanceof LdfClassType) {
                numClass++;
            } else {
                numForeign++;
            }
        }

        if (numArrays > 0) {
            if (numArrays != numTypes) return NoType.INSTANCE;
            DataType dt = null;
            for (DataType t : types) {
                if (dt == null) {
                    dt = t;
                } else {
                    if (!dt.equals(t)) {
                        return NoType.INSTANCE;
                    }
                }
            }
            return dt;
        }

        if (numObject > 0) {
            if (numObject != numTypes) return NoType.INSTANCE;
            DataType dt = null;
            for (DataType t : types) {
                if (dt == null) {
                    dt = t;
                } else {
                    dt = dt.getLeastUpperBound(t);
                }
            }
            return dt;
        }

        if (numClass > 0) {
            if (numClass != numTypes) return NoType.INSTANCE;
            DataType dt = null;
            for (DataType t : types) {
                if (dt == null) {
                    dt = t;
                } else {
                    dt = dt.getLeastUpperBound(t);
                }
            }
            return dt;
        }

        if (numForeign > 0) {
            if (numForeign != numTypes) return NoType.INSTANCE;
            // ... nothing to do here
        }

        return null;
    }
}
