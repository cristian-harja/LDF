package ldf.parser.inspect;

import ldf.parser.Context;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.TreeSet;

import static ldf.parser.Util.NATIVE_HASH_COMPARATOR;

/**
 * Provided for convenience; a {@link java.util.Set} of {@link
 * ldf.parser.inspect.Inspection} objects.
 *
 * @author Cristian Harja
 */
@ParametersAreNonnullByDefault
public final class InspectionSet<T>
        extends TreeSet<Inspection<Context, ? extends T>>{

    public InspectionSet() {
        super(NATIVE_HASH_COMPARATOR);
    }

    /**
     * Runs <em>all inspections</em> contained in this set over <em>all
     * the elements</em> available through the iterator.
     */
    public void runAllOnIterator(
            @Nonnull Context ctx,
            @Nonnull Iterator<T> it
    ) {
        while (it.hasNext()) {
            T obj = it.next();

            for (Inspection<Context, ?> i: this) {
                i.run(ctx, obj);
            }
        }
    }


}
