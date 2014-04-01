package ldf.parser.ast.expr;

/**
 * Unary operators (to be used by {@link ExprUnary}).
 *
 * @author Cristian Harja
 * @see ExprUnary
 */
public enum UnaryOp {
    PLUS("+"),
    MINUS("-"),
    NOT("!");

    private String op;

    UnaryOp(String op) {
        this.op = op;
    }

    public String getOperatorAsText() {
        return op;
    }
}
