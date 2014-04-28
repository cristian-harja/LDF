package ldf.parser.ast.type;

import ldf.parser.ast.Reference;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class ClassTypeExpr extends TypeExpression {

    @Nonnull
    private Reference reference;

    public ClassTypeExpr(@Nonnull Reference ref) {
        reference = ref;
        addAstChildren(ref);
    }

    @Nonnull
    public Reference getReference() {
        return reference;
    }
}
