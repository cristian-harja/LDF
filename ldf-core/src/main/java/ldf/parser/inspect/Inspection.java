package ldf.parser.inspect;

import javax.annotation.Nonnull;

/**
 * <p>Defines the concept of an "inspection" -- a command which can
 * analyze an object and then issue a report about it (an error, warning,
 * information, etc.).</p>
 *
 * <p>Before executing the inspection, the {@link #filter} method is
 * called to check whether this inspection is applicable for the given
 * object. If it is, then the {@code protected abstract} {@link #inspect}
 * method is called to analyze the object.</p>
 *
 * @param <ContextT> the type of the context variable that gets passed
 * @param <TargetT> the type of objects targeted by this inspection
 *
 * @author Cristian Harja
 */
public abstract class Inspection<ContextT, TargetT> {

    private final Class<TargetT> clazz;

    /**
     * @param clazz the type of objects targeted by this inspection
     */
    protected Inspection(@Nonnull Class<TargetT> clazz) {
        this.clazz = clazz;
    }

    /**
     * <p>Executes the inspection on the given object. First checks the
     * object's class (must be a sub-class of the one given as parameter
     * to the constructor), then calls the {@link #filter} method.</p>
     *
     * <p>If both tests pass, the {@link #inspect} method is called.</p>
     *
     * @return whether the inspection has been executed
     */
    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public final boolean run(ContextT ctx, @Nonnull Object obj) {
        if (clazz.isAssignableFrom(obj.getClass())) {
            TargetT checked = (TargetT) obj;
            return filter(ctx, checked) && inspect(ctx, checked);
        } else {
            return false;
        }
    }

    /**
     * Having checked the object's class, this method filters out
     * objects which are irrelevant to this inspection. By returning
     * {@code false}, they won't be passed to the {@link #inspect} method.
     */
    protected boolean filter(ContextT ctx, @Nonnull TargetT obj) {
        return true;
    }

    /**
     * Performs the actual inspection.
     *
     * @param ctx context information
     * @param obj object being inspected; {@link #filter filter()}
     *            already returned {@code true} on it.
     * @return whether the inspection has been executed (it can still skip
     *         objects which were not {@link #filter filter()}ed out)
     */
    protected abstract boolean inspect(ContextT ctx, @Nonnull TargetT obj);
}
