package ldf.parser.ast.stmt;

import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * {@code continue;} statement.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtContinue extends Statement {
    private AstIdentifier label;

    /**
     * @param label optional label (pointing to the loop to be continued).
     */
    public StmtContinue(@Nullable AstIdentifier label) {
        this.label = label;
        addAstChildren(label);
    }

    public AstIdentifier getLabel() {
        return label;
    }
}
