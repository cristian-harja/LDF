package ldf.parser.ast.stmt;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * A list of statements, within a pair of brackets (ex: {@code { ... }} ).
 * Backed by the {@code stmt_block} non-terminal in the grammar.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class StmtBlock extends Statement {

    @Nonnull
    private StmtList list;

    public StmtBlock(@Nonnull StmtList list) {
        this.list = list;
        addAstChildren(list);
    }

    private StmtBlock() {}

    @Nonnull
    public List<Statement> getStatements() {
        return list.getItems();
    }

}
