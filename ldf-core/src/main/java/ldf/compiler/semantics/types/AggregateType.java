package ldf.compiler.semantics.types;

import java.util.SortedMap;

/**
 * @author Cristian Harja
 */
public abstract class AggregateType extends DataType {
    @Override
    public final boolean isAggregate() {
        return true;
    }

    public abstract SortedMap<String, DataType> getFields();
}
