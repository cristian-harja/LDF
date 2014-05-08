package ldf.compiler.semantics.types;

import ldf.compiler.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.*;

/**
 * @author Cristian Harja
 */
@Immutable
public class ObjectType extends AggregateType {

    protected final SortedMap<String, DataType> fields;

    protected final SortedMap<String, DataType> readOnlyFields;

    protected ObjectType() {
        fields = new TreeMap<String, DataType>();
        readOnlyFields = Collections.unmodifiableSortedMap(fields);
    }

    public SortedMap<String, DataType> getFields() {
        return readOnlyFields;
    }

    @Override
    protected boolean isAssignableFromImpl(@Nonnull DataType t) {
        if (equals(t)) {
            return true;
        }
        if (!(t instanceof ObjectType)) {
            return false;
        }
        for (Map.Entry<String, DataType> e :
                ((ObjectType) t).fields.entrySet()) {
            DataType f = fields.get(e.getKey());
            if (f == null) {
                return false;
            }
            if (!f.isAssignableFrom(e.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public DataType getLeastUpperBound(DataType t) {
        if (!(t instanceof ObjectType)) {
            return NoType.INSTANCE;
        }
        ObjectType objType = (ObjectType) t;

        if (objType.fields.size() == fields.size()) {
            if (objType.fields.entrySet().containsAll(fields.entrySet()) ||
                fields.entrySet().containsAll(objType.fields.entrySet())
            ) {
                return this;
            }
        }

        ObjectType result = new ObjectType();
        result.fields.putAll(fields);
        result.fields.putAll(objType.fields);

        SortedSet<String> commonFields = new TreeSet<String>();
        commonFields.addAll(fields.keySet());
        commonFields.retainAll(objType.fields.keySet());

        for (String key: commonFields) {
            DataType t1 = this.fields.get(key);
            DataType t2 = objType.fields.get(key);
            DataType lub = t1.getLeastUpperBound(t2);
            if (lub == NoType.INSTANCE) {
                return NoType.INSTANCE;
            }
            result.fields.put(key, lub);
        }

        return result;
    }

    @Override
    public void format(@Nonnull Appendable out) throws IOException {
        boolean first = true;
        out.append('{');
        for (SortedMap.Entry<String, DataType> e: fields.entrySet()) {
            if (!first) {
                out.append(',');
            }
            out.append(e.getKey());
            out.append(':');
            e.getValue().format(out);
            first = false;
        }
        out.append('}');
    }

    @Override
    protected boolean equals(@Nonnull DataType t) {
        return t == this || t instanceof ObjectType &&
                compareFields((ObjectType) t);
    }

    private boolean compareFields(ObjectType that) {
        Iterator<SortedMap.Entry<String, DataType>> it1, it2;
        SortedMap.Entry<String, DataType> e1, e2;

        if (fields.size() != that.fields.size()) {
            return false;
        }

        it1 = this.fields.entrySet().iterator();
        it2 = that.fields.entrySet().iterator();

        while (it1.hasNext()) {
            e1 = it1.next();
            e2 = it2.next();

            if (!e1.getKey().equals(e2.getKey())) {
                return false;
            }
            if (!e1.getValue().equals(e2.getValue())) {
                return false;
            }
        }
        return true;
    }

    public static final class Builder {

        private ObjectType obj = new ObjectType();
        private boolean built;

        public Builder add(String field, DataType type) {
            Util.assertNotBuilt(built, ObjectType.class);
            obj.fields.put(field, type);
            return this;
        }

        public ObjectType build() {
            built = true;
            return obj;
        }

    }

}
