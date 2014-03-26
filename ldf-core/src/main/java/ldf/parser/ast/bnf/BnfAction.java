package ldf.parser.ast.bnf;

import ldf.parser.ast.stmt.StmtList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Grammar action to be executed when a production has been recognized in
 * the input. Example: {@code {: ... :}}. The code has access to the
 * recognized (labeled) symbols of the production.
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfAction implements BnfAbstractAction {

    @Nonnull
    private StmtList stmtList;

    /**
     * @param stmtList the code of the grammar action
     */
    public BnfAction(
            @Nonnull StmtList stmtList
    ){
        this.stmtList = stmtList;
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
}
