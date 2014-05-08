package ldf.compiler.semantics.types;

import ldf.compiler.ast.Reference;
import ldf.compiler.ast.type.TypeExpression;

import java.util.Collection;

/**
 * @author Cristian Harja
 */
public interface TypeEnv {

    DataType resolveType(TypeExpression type);

    DataType computeLeastUpperBound(Collection<DataType> types);

    DataType resolveClass(Reference ref);

    DataType getStringDataType();

}
