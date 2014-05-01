package ldf.compiler.semantics.types;

import ldf.parser.ast.type.TypeExpression;

/**
 * @author Cristian Harja
 */
public interface TypeEnv {

    DataType resolveType(TypeExpression type);

    DataType computeLeastUpperBound(DataType... types);

}
