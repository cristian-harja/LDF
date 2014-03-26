package ldf.parser.ast.bnf;

import ldf.parser.ast.expr.Expression;

import javax.annotation.Nonnull;

/**
 * A type of grammar action (similar to {@link BnfAction}), containing a
 * boolean expression. If (while parsing) the expression evaluates to
 * {@literal false}, the production being parsed should be discarded (the
 * parser will probably backtrack to a new one).
 *
 * @author Cristian Harja
 */
public final class BnfGuard implements BnfAbstractAction {

    @Nonnull
    private Expression condition;

    /**
     * @param condition the condition to be evaluated at runtime
     */
    public BnfGuard(@Nonnull Expression condition) {
        this.condition = condition;
    }

    @Nonnull
    public Expression getCondition() {
        return condition;
    }

    /**
     * @return {@link BnfAtomType#GUARD}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.GUARD;
    }
}
