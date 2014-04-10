package ldf.parser.ast.stmt;

import ldf.parser.ast.decl.DeclFunction;
import ldf.parser.decl.SymbolType;

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

    @Override
    public boolean hasOwnScope() {
        return getAstParent() instanceof DeclFunction;
    }

    @Override
    protected int getAcceptedTypes() {
        return SymbolType.VARIABLE.bitMask;
    }
}
