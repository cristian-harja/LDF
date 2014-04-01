package ldf.parser.inspect;

/**
 * @author Cristian Harja
 */
@SuppressWarnings("unchecked")
public abstract class Predicate<T> {
    public abstract boolean eval(T target);

    /**
     * A predicate which always evaluates to {@code true}.
     */
    public static <T> Predicate<T> tautology() {
        return (Predicate<T>)TAUTOLOGY;
    }

    /**
     * A predicate which always evaluates to {@code false}.
     */
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
