package ldf.compiler.ast.expr;

import javax.annotation.Nonnull;

import static ldf.compiler.ast.expr.BinaryOpClass.*;

/**
 * <p>Defines (most or all) binary operators which can occur in LDF as part
 * of an expression.</p>
 *
 * <p>The values in this enum are used by {@link ExprCompound} to denote
 * separators in an expression (ex. in an expression like {@code a + b - c}
 * the separators are {@code +} and {@code -}, denoted by {@link #ALG_ADD}
 * and {@link #ALG_SUB} respectively).</p>
 *
 * <p>Each value in this enum also contains a reference to a {@link
 * BinaryOpClass}, which indicates the class to which this operator belongs
 * (ex: additive operator, comparison, etc.). This information is available
 * through the {@link #getOperatorClass()} method.</p>
 *
 * @see ExprCompound
 * @see BinaryOpClass
 * @author Cristian Harja
 */
public enum BinaryOp {
    /**
     * Assignment: {@code =}
     */
    ASSIGN  ("=",  ASSIGNMENT),

    /**
     * Boolean OR: {@code ||}
     */
    BOOL_OR ("||", BOOL_ADDITIVE),

    /**
     * Boolean AND: {@code &&}
     */
    BOOL_AND("&&", BOOL_MULTIPLICATIVE),

    /**
     * Comparison (greater than): {@code >}
     */
    CMP_GT  (">",  COMPARISON),

    /**
     * Comparison (greater than or equal): {@code >=}
     */
    CMP_GTE (">=", COMPARISON),

    /**
     * Comparison (lesser than): {@code <}
     */
    CMP_LT  ("<",  COMPARISON),

    /**
     * Comparison (lesser than or equal): {@code <=}
     */
    CMP_LTE ("<=", COMPARISON),

    /**
     * Comparison (equal to): {@code ==}
     */
    CMP_EQ  ("==", COMPARISON),

    /**
     * Comparison (not equal to): {@code !=}
     */
    CMP_NEQ ("!=", COMPARISON),

    /**
     * Algebraic addition: {@code +}
     */
    ALG_ADD ("+",  ADDITIVE),

    /**
     * Algebraic subtraction: {@code -}
     */
    ALG_SUB ("-",  ADDITIVE),

    /**
     * Algebraic multiplication: {@code *}
     */
    ALG_MUL ("*",  MULTIPLICATIVE),

    /**
     * Algebraic division: {@code /}
     */
    ALG_DIV ("/",  MULTIPLICATIVE),

    /**
     * Algebraic remainder: {@code %}
     */
    ALG_MOD ("%",  MULTIPLICATIVE);

    private BinaryOpClass cls;
    private String op;

    private BinaryOp(String op, BinaryOpClass cls) {
        this.cls = cls;
        this.op = op;
    }

    /**
     * @return a {@link BinaryOpClass} value, indicating the classification
     *         of this operator (ex: additive, comparation, etc.).
     */
    @Nonnull
    public BinaryOpClass getOperatorClass() {
        return cls;
    }

    /**
     * @return a text representation of this operator, as found in the
     *         source code.
     */
    @Nonnull
    public String getOperatorAsText() {
        return op;
    }
}
