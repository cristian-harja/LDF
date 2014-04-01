package ldf.parser.ast.expr;

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
    private ExprReference classRef;

    @Nonnull
    private ExprList ctorParams;

    public ExprNew(
            @Nonnull ExprReference classRef,
            @Nonnull ExprList ctorParams
    ) {
        this.classRef = classRef;
        this.ctorParams = ctorParams;
        addAstChildren(classRef, ctorParams);
    }

    @Nonnull
    public ExprReference getClassRef() {
        return classRef;
    }

    @Nonnull
    public ExprList getCtorParams() {
        return ctorParams;
    }
}
