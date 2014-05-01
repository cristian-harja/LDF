package ldf.compiler.ast.expr;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static ldf.compiler.ast.expr.BinaryOp.*;

/**
 * <p>Divides the operators in {@link BinaryOp} into categories.</p>
 *
 * <p>Used by {@link ExprCompound} to denote the type of a sub-expression
 * (all the <em>direct</em> descendants of an {@code ExprCompond} can only
 * be combined with operators from the same category).</p>
 *
 * <p>For example: a multiplicative expression could be {@code a*b/c%d}.
 * The direct descendants are a, b, c and d. The operators (multiplication,
 * division and modulo) are part of the same class. As long as this
 * condition is met, the 4 descendants can be any sub-expressions. A
 * violation of this rule would be a consequence of an incorrect parser!
 * </p>
 *
 * @see BinaryOp
 *
 * @author Cristian Harja
 */
public enum BinaryOpClass {
    /**
     * Assignment:
     * {@link BinaryOp#ASSIGN =}
     */
    ASSIGNMENT(ASSIGN),

    /**
     * Boolean additive operator:
     * {@link BinaryOp#BOOL_OR ||}.
     */
    BOOL_ADDITIVE(BOOL_OR),

    /**
     * Boolean multiplicative operator:
     * {@link BinaryOp#BOOL_AND &&}.
     */
    BOOL_MULTIPLICATIVE(BOOL_AND),

    /**
     * Comparison operators:
     * {@link BinaryOp#CMP_EQ  ==},
     * {@link BinaryOp#CMP_NEQ !=},
     * {@link BinaryOp#CMP_LT  < },
     * {@link BinaryOp#CMP_LTE <=},
     * {@link BinaryOp#CMP_GT  > },
     * {@link BinaryOp#CMP_GTE >=}.
     */
    COMPARISON(CMP_EQ, CMP_NEQ, CMP_LT,CMP_LTE, CMP_GT, CMP_GTE),

    /**
     * Algebraic additive operators:
     * {@link BinaryOp#ALG_ADD +},
     * {@link BinaryOp#ALG_SUB -}.
     */
    ADDITIVE(ALG_ADD, ALG_SUB),

    /**
     * Algebraic multiplicative operators:
     * {@link BinaryOp#ALG_MUL *},
     * {@link BinaryOp#ALG_DIV /}.
     * {@link BinaryOp#ALG_MOD %}.
     */
    MULTIPLICATIVE(ALG_MUL, ALG_DIV, ALG_MOD);

    private List<BinaryOp> operators;

    private BinaryOpClass(BinaryOp... operators) {
        this.operators = unmodifiableList(asList(operators));
    }

    /**
     * @return a list of operators, which belong to this class
     */
    public List<BinaryOp> getOperators() {
        return operators;
    }
}
