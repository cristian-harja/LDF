package ldf.parser.ast.bnf;

import ldf.parser.ast.AstNode;
import ldf.parser.ast.stmt.StmtList;
import ldf.parser.decl.SymbolType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Grammar action to be executed when a production has been recognized in
 * the input. Example: {@code {: ... :}}. The code has access to the
 * recognized (labeled) symbols of the production.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class BnfAction extends AstNode
    implements BnfAbstractAction {

    @Nonnull
    private StmtList stmtList;

    /**
     * @param stmtList the code of the grammar action
     */
    public BnfAction(
            @Nonnull StmtList stmtList
    ){
        this.stmtList = stmtList;
        addAstChildren(stmtList);
    }

    @Nonnull
    public StmtList getStmtList() {
        return stmtList;
    }

    /**
     * @return {@link BnfAtomType#ACTION}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.ACTION;
    }

    @Override
    public boolean hasOwnScope() {
        return true;
    }

    @Override
    protected int getAcceptedTypes() {
        return SymbolType.VARIABLE.bitMask;
    }
}
