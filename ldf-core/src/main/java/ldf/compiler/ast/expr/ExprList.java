package ldf.compiler.ast.expr;

import ldf.compiler.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;

import static ldf.compiler.Util.ListBuilder;

/**
 * A list of expressions separated by commas. This is not an expression
 * by itself, but used to represent the list of actual parameters in a
 * function call.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class ExprList extends AstNode {

    @Nonnull
    private List<Expression> items;

    @Nonnull
    public List<Expression> getItems() {
        return items;
    }

    private ExprList(
            @Nonnull List<Expression> items
    ) {
        this.items = items;
        addAstChildren(items);
    }

    public ExprList() {
        items = Collections.emptyList();
    }

    /**
     * Builds {@link ExprList} objects.
     */
    @NotThreadSafe
    public static class Builder extends ListBuilder<Expression, Builder>{

        public ExprList build() {
            return new ExprList(buildList());
        }
    }

}
