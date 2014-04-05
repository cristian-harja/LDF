package ldf.parser.util;

import javax.annotation.concurrent.Immutable;

/**
 * A predicate is a function which takes some parameters and returns a
 * {@literal true} or {@literal false} value. The purpose of a predicate
 * is to state whether or not the arguments meet a certain condition; it
 * <em>must not</em> alter the state of those objects, or the state of
 * the program.
 *
 * @param <T> the type of objects on which this predicate operates
 *
 * @author Cristian Harja
 */
@Immutable
public abstract class Predicate<T> {

    /**
     * Evaluate the predicate on the given argument.
     */
    public abstract boolean eval(T target);

    /**
     * A predicate which always evaluates to {@code true}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> tautology() {
        return (Predicate<T>)TAUTOLOGY;
    }

    /**
     * A predicate which always evaluates to {@code false}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> contradiction() {
        return (Predicate<T>)CONTRADICTION;
    }

    private static Predicate TAUTOLOGY = new Predicate() {
        @Override
        public boolean eval(Object target) {
            return true;
        }
    };

    private static Predicate CONTRADICTION = new Predicate() {
        @Override
        public boolean eval(Object target) {
            return false;
        }
    };
}
