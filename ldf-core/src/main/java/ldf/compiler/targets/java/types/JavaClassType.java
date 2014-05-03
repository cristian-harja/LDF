package ldf.compiler.targets.java.types;

import ldf.compiler.semantics.types.DataType;
import ldf.compiler.semantics.types.NoType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.*;

import static com.google.common.collect.Iterables.getOnlyElement;

/**
 * @author Cristian Harja
 */
@Immutable
public final class JavaClassType extends DataType {

    private Class<?> clazz;

    public JavaClassType(@Nonnull Class<?> cls) {
        this.clazz = cls;
    }

    @Override
    public boolean isAggregate() {
        return true;
    }

    @Override
    protected boolean isAssignableFromImpl(DataType t) {
        return t instanceof JavaClassType &&
                clazz.isAssignableFrom(((JavaClassType) t).clazz);
    }

    @Override
    public DataType getLeastUpperBound(DataType t) {
        if (!(t instanceof JavaClassType)) {
            return NoType.INSTANCE;
        }
        Collection<Class<?>> bounds;
        JavaClassType classType = (JavaClassType) t;

        bounds = getLeastUpperBounds(clazz, classType.clazz);

        if (bounds.size() != 1) {
            return NoType.INSTANCE;
        }

        return new JavaClassType(getOnlyElement(bounds));
    }

    public static Collection<Class<?>> getLeastUpperBounds(
            Class<?> ... classes
    ) {
        return getLeastUpperBounds(Arrays.asList(classes));
    }

    public static Collection<Class<?>> getLeastUpperBounds(
            Collection<Class<?>> classes
    ) {
        Set<Class<?>> upperBounds = intersectUpperBounds(
                classes, Collections.<Class<?>>emptySet()
        );

        if (upperBounds == null) {
            throw new IllegalArgumentException("Empty argument list");
        }

        return intersectUpperBounds(classes, upperBounds);
    }

    private static Set<Class<?>> intersectUpperBounds(
            Collection<Class<?>> classes, Set<Class<?>> stopAt
    ) {
        Set<Class<?>> upperBounds = null;
        for (Class<?> c : classes) {
            if (upperBounds == null) {
                upperBounds = getUpperBounds(c, stopAt);
            } else {
                upperBounds.retainAll(getUpperBounds(c, stopAt));
            }
        }
        return upperBounds;
    }

    private static Set<Class<?>> getUpperBounds(
            Class<?> cls, Set<Class<?>> stopAt
    ) {
        Set<Class<?>> set1 = new LinkedHashSet<Class<?>>();
        Set<Class<?>> set2 = new LinkedHashSet<Class<?>>();
        Set<Class<?>> set3 = new LinkedHashSet<Class<?>>();
        Set<Class<?>> aux;
        set1.add(cls);
        while (!set1.isEmpty()) {
            set1.remove(null);
            for (Class<?> c: set1) {
                if (stopAt.contains(c)) {
                    continue;
                }
                set2.add(c.getSuperclass());
                set2.addAll(Arrays.asList(c.getInterfaces()));
            }
            set3.addAll(set1);
            set2.removeAll(set3);
            set1.clear();
            aux = set2;
            set2 = set1;
            set1 = aux;
        }
        return set3;
    }

    @Override
    public void format(@Nonnull Appendable out) throws IOException {
        out.append(clazz.getCanonicalName());
    }

    @Override
    protected boolean equals(@Nonnull DataType t) {
        return t == this || t instanceof JavaClassType && clazz.equals(
                ((JavaClassType) t).clazz
        );
    }
}
