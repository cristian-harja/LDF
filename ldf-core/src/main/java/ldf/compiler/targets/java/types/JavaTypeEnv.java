package ldf.compiler.targets.java.types;

import ldf.compiler.semantics.types.AbstractTypeEnv;
import ldf.compiler.semantics.types.DataType;
import ldf.compiler.semantics.types.NoType;
import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.type.ClassTypeExpr;

import java.util.List;

/**
 * @author Cristian Harja
 */
public class JavaTypeEnv extends AbstractTypeEnv {

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
    public DataType computeLeastUpperBound(DataType... types) {
        DataType lub = null;
        if (types.length == 0) {
            throw new IllegalArgumentException("Empty list of agruments");
        }
        for (DataType t : types) {
            if (lub == null) {
                lub = t;
                continue;
            }
            lub = lub.getLeastUpperBound(t);
            if (lub == NoType.INSTANCE) {
                return lub;
            }
        }
        return lub;
    }
}
