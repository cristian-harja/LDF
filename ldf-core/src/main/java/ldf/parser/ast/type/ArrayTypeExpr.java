package ldf.parser.ast.type;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class ArrayTypeExpr extends TypeExpression {
    @Nonnull
    private TypeExpression baseType;

    public ArrayTypeExpr(@Nonnull TypeExpression baseType) {
        this.baseType = baseType;
        addAstChildren(baseType);
    }

    @Nonnull
    public TypeExpression getBaseType() {
        return baseType;
    }
}
