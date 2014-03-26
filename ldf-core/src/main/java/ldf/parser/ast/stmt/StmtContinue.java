package ldf.parser.ast.stmt;

import javax.annotation.Nullable;

/**
 * {@code continue;} statement.
 *
 * @author Cristian Harja
 */
public final class StmtContinue implements Statement {
    private String label;

    /**
     * @param label optional label (pointing to the loop to be continued).
     */
    public StmtContinue(@Nullable String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
