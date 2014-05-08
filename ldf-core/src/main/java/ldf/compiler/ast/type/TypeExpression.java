package ldf.compiler.ast.type;

import ldf.compiler.ast.AstNode;
import ldf.compiler.semantics.types.DataType;
import ldf.compiler.util.Util;

/**
 * @author Cristian Harja
 */
public abstract class TypeExpression extends AstNode {

    private DataType dataType;

    public void setDataType(DataType dataType) {
        Util.assertSetOnce(this.dataType, "setDataType");
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }
}
