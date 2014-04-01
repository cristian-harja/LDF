package ldf.parser.inspect;

/**
 * @author Cristian Harja
 */
public abstract class Inspection<TargetT> {

    public Predicate<TargetT> getFilteringPredicate() {
        return Predicate.tautology();
    }

    public abstract void runOn(TargetT obj, Receiver<TargetT> recv);
}
