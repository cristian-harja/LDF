package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * AST node for object creation (using the {@code new} keyword).
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprNew implements Expression {

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
