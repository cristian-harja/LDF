package ldf.parser.ast.stmt;

import javax.annotation.Nullable;

/**
 * {@code break;} statement.
 *
 * @author Cristian Harja
 */
public final class StmtBreak implements Statement {
    private String label;

    /**
     * @param label optional label (pointing to the loop that should be
     *              exited).
     */
    public StmtBreak(@Nullable String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
