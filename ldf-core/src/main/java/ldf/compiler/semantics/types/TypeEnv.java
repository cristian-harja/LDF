package ldf.compiler.semantics.types;

import ldf.compiler.ast.type.TypeExpression;

/**
 * @author Cristian Harja
 */
public interface TypeEnv {

    DataType resolveType(TypeExpression type);

    DataType computeLeastUpperBound(DataType... types);

}
