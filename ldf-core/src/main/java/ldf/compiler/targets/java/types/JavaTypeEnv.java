package ldf.compiler.targets.java.types;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.type.ClassTypeExpr;
import ldf.compiler.semantics.types.AbstractTypeEnv;
import ldf.compiler.semantics.types.DataType;
import ldf.compiler.semantics.types.NoType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static ldf.compiler.targets.java.types.JavaClassType.getLeastUpperBounds;

/**
 * @author Cristian Harja
 */
public class JavaTypeEnv extends AbstractTypeEnv {

    DataType stringClass = new JavaClassType(String.class);

    @Override
    protected DataType resolveClass(ClassTypeExpr astNode) {

        // TODO: make use of a search path ?

        List<AstIdentifier> path = astNode.getReference().getPath();
        StringBuilder sb = new StringBuilder();

        for (AstIdentifier id: path) {
            if (sb.length() != 0) {
                sb.append('.');
            }
            sb.append(id.getName());
        }

        try {
            return new JavaClassType(Class.forName(sb.toString()));
        } catch (ClassNotFoundException e) {
            return NoType.INSTANCE;
        }
    }

    @Override
    public DataType computeLeastUpperBound(Collection<DataType> types) {
        // fixme: better design ?
        DataType lub = DataType.tryComputeLeastUpperBound(types);
        if (lub != null) return lub;

        if (types.size() == 0) {
            throw new IllegalArgumentException("Empty list of types");
        }
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (DataType t : types) {
            if (!(t instanceof JavaClassType)) {
                return NoType.INSTANCE;
            }
            JavaClassType cls = (JavaClassType) t;
            classes.add(cls.getWrappedClass());
        }
        Collection<Class<?>> bounds = getLeastUpperBounds(classes);

        if (bounds.size() == 1) {
            return new JavaClassType(getOnlyElement(bounds));
        }

        return NoType.INSTANCE;
    }

    @Override
    public DataType resolveClass(Reference ref) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        Iterator<AstIdentifier> it = ref.getPath().iterator();

        while (it.hasNext()) {
            String name = it.next().getName();
            if (!first) {
                sb.append('.');
            }
            first = false;
            sb.append(name);
            try {
                return new JavaClassType(Class.forName(sb.toString()));
            } catch (ClassNotFoundException ignored) {
                continue;
            }
        }

        Class<?> cls = null;

        while (it.hasNext()) {
            String name = it.next().getName();
            sb.append('$');
            sb.append(name);
            try {
                cls = Class.forName(sb.toString());
            } catch (ClassNotFoundException ignored) {
                return null;
            }
        }

        if (cls == null) return null;

        return new JavaClassType(cls);
    }

    @Override
    public DataType getStringDataType() {
        return stringClass;
    }
}
