package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.List;

import static ldf.parser.Util.ListBuilder;

/**
 * A list of expressions separated by commas. This is not an expression
 * by itself, but used to represent the list of actual parameters in a
 * function call.
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprList {

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
