package ldf.parser.ast.expr;

import ldf.parser.ast.Reference;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * AST node for object creation (using the {@code new} keyword).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class ExprNew extends Expression {

    @Nonnull
    private Reference classRef;

    @Nonnull
    private ExprList ctorParams;

    public ExprNew(
            @Nonnull Reference classRef,
            @Nonnull ExprList ctorParams
    ) {
        this.classRef = classRef;
        this.ctorParams = ctorParams;
        addAstChildren(classRef, ctorParams);
    }

    @Nonnull
    public Reference getClassRef() {
        return classRef;
    }

    @Nonnull
    public ExprList getCtorParams() {
        return ctorParams;
    }
}
