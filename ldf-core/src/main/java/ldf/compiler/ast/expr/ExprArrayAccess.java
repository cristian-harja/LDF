package ldf.compiler.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Array access. The index is not necessarily an integer (as far as the
 * syntax of the language is concerned).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class ExprArrayAccess extends Expression {

    @Nonnull
    private final Expression array;

    @Nonnull
    private final Expression index;

    public ExprArrayAccess(
            @Nonnull Expression array,
            @Nonnull Expression index
    ) {
        this.array = array;
        this.index = index;
        addAstChildren(array, index);
    }

    @Nonnull
    public Expression getArray() {
        return array;
    }

    @Nonnull
    public Expression getIndex() {
        return index;
    }
}
