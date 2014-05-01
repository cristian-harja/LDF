package ldf.compiler.inspect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.TreeSet;

import static ldf.compiler.util.Util.NATIVE_HASH_COMPARATOR;

/**
 * Provided for convenience; a {@link java.util.Set} of {@link
 * Inspection} objects.
 *
 * @author Cristian Harja
 */
public final class InspectionSet<ContextT, TargetT>
        extends TreeSet<Inspection<? super ContextT, ? extends TargetT>>{

    public InspectionSet() {
        super(NATIVE_HASH_COMPARATOR);
    }

    /**
     * Runs <em>all inspections</em> contained in this set over <em>all
     * the elements</em> available through the iterator.
     */
    public void runAllOnIterator(
            @Nullable ContextT ctx,
            @Nonnull Iterator<TargetT> it
    ) {
        while (it.hasNext()) {
            TargetT obj = it.next();

            for (Inspection<? super ContextT, ?> i: this) {
                i.run(ctx, obj);
            }
        }
    }


}
