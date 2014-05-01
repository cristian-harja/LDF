package ldf.compiler.semantics.types;

import ldf.compiler.ast.type.ArrayTypeExpr;
import ldf.compiler.ast.type.ClassTypeExpr;
import ldf.compiler.ast.type.ObjectTypeExpr;
import ldf.compiler.ast.type.TypeExpression;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cristian Harja
 */
public abstract class AbstractTypeEnv implements TypeEnv {

    protected Map<String, AggregateType> cacheObjTypes;

    protected AbstractTypeEnv() {
        cacheObjTypes = new HashMap<String, AggregateType>();
    }

    public DataType resolveType(TypeExpression type) {
        if (type instanceof ArrayTypeExpr) {
            return makeArrayType((ArrayTypeExpr) type);
        } else if (type instanceof ObjectTypeExpr) {
            return makeObjectType((ObjectTypeExpr) type);
        } else if (type instanceof ClassTypeExpr) {
            return resolveClass((ClassTypeExpr) type);
        } else {
            throw new RuntimeException(
                    "Can't interpret AST node as type expression: " +
                    type.getClass().getCanonicalName()
            );
        }
    }

    protected abstract DataType resolveClass(ClassTypeExpr astNode);

    protected DataType makeArrayType(ArrayTypeExpr astNode) {
        return resolveType(astNode.getBaseType()).getArrayType();
    }

    protected DataType makeObjectType(ObjectTypeExpr astNode) {
        ObjectType type = new ObjectType();
        Map<String, DataType> fields = type.fields;
        for (ObjectTypeExpr.Entry e: astNode.getEntries()) {
            // TODO: check for duplicates?
            fields.put(e.getId().getName(), resolveType(e.getType()));
        }
        return type;
    }

}
