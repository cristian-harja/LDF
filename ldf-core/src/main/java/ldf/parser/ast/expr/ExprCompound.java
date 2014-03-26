package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * AST node for expressions which use a few (binary) operators to combine
 * an arbitrary number of sub-expressions into one. Example: {@code
 * a + b - c - d + e} (uses + and - to combine 5 elements).
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprCompound implements Expression {

    @Nonnull
    private BinaryOpClass expressionType;

    @Nonnull
    private List<Expression> items;

    @Nonnull
    private List<BinaryOp> separators;

    private ExprCompound(
            @Nonnull BinaryOpClass expressionType,
            @Nonnull List<Expression> items,
            @Nonnull List<BinaryOp> separators
    ) {
        this.expressionType = expressionType;
        this.items = unmodifiableList(items);
        this.separators = unmodifiableList(separators);
    }

    /**
     * @return the type of this expression
     */
    @Nonnull
    public BinaryOpClass getExpressionType() {
        return expressionType;
    }

    /**
     * @return the items (direct descendants) of this expression
     */
    @Nonnull
    public List<Expression> getItems() {
        return items;
    }

    /**
     * @return separators (operators) between every consecutive two items
     */
    @Nonnull
    public List<BinaryOp> getSeparators() {
        return separators;
    }

    /**
     * Builds {@link ExprCompound} objects.
     */
    @NotThreadSafe
    public static class Builder {
        private BinaryOpClass type;
        private List<Expression> items = new ArrayList<Expression>();
        private List<BinaryOp> separators = new ArrayList<BinaryOp>();

        public Builder init(
                @Nonnull BinaryOpClass cls,
                @Nonnull Expression expr
        ) {
            type = cls;
            items.clear();
            separators.clear();
            items.add(expr);
            return this;
        }

        public Builder add(
                @Nonnull BinaryOp separator,
                @Nonnull Builder expr
        ) {
            return add(separator, expr.build());
        }

        public Builder add(
                @Nonnull BinaryOp separator,
                @Nonnull Expression expr
        ) {
            separators.add(separator);
            items.add(expr);
            return this;
        }

        public Expression build() {
            if (items.size() == 1) {
                return items.get(0);
            }
            return new ExprCompound(type, items, separators);
        }

    }

}
