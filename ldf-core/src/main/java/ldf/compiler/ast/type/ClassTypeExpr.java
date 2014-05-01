package ldf.compiler.ast.type;

import ldf.compiler.ast.Reference;

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
