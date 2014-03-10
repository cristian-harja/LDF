package ldf.parser.ast;

import javax.annotation.Nonnull;

/**
 * A type of grammar action (similar to {@link BnfAction BnfAction}), that
 * contains a boolean expression. If (while parsing) the expression
 * evaluates to {@code false}, the production being parsed should be
 * discarded (the parser will probably backtrack to a new one).
 *
 * @author Cristian Harja
 */
public final class BnfGuard extends BnfAbstractAction {

    @Nonnull
    private Expression condition;

    public BnfGuard(@Nonnull Expression condition) {
        this.condition = condition;
    }

    @Nonnull
    public Expression getCondition() {
        return condition;
    }

    public void setCondition(@Nonnull Expression condition) {
        this.condition = condition;
    }

    @Override
    public Type getType() {
        return Type.GUARD;
    }
}
