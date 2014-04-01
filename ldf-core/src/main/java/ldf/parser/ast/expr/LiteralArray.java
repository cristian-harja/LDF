package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Array literal (ex: {@code [1, 2, 5, "Alice", "Bob"]}).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class LiteralArray extends ExprLiteral {

    @Nonnull
    private ExprList elements;

    public LiteralArray(
            @Nonnull ExprList elements
    ){
        this.elements = elements;
        addAstChildren(elements);
    }

    @Nonnull
    public ExprList getElements() {
        return elements;
    }
}
