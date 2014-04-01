package ldf.parser.ast.stmt;

import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * {@code break;} statement.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtBreak extends Statement {
    private AstIdentifier label;

    /**
     * @param label optional label (pointing to the loop that should be
     *              exited).
     */
    public StmtBreak(@Nullable AstIdentifier label) {
        this.label = label;
        addAstChildren(label);
    }

    public AstIdentifier getLabel() {
        return label;
    }
}
