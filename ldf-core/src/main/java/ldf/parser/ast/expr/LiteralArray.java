package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Array literal (ex: {@code [1, 2, 5, "Alice", "Bob"]}).
 *
 * @author Cristian Harja
 */
@Immutable
public final class LiteralArray implements ExprLiteral {

    @Nonnull
    private ExprList elements;

    public LiteralArray(
            @Nonnull ExprList elements
    ){
        this.elements = elements;
    }

    @Nonnull
    public ExprList getElements() {
        return elements;
    }
}
