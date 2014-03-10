package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Expresses a grammar action (code) to be executed when a production has
 * been recognized in the input. The code has access to the recognized
 * elements of the production.
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfAction extends BnfAbstractAction {

    @Nonnull
    private StmtList stmtList;

    public BnfAction(
            @Nonnull StmtList stmtList
    ){
        this.stmtList = stmtList;
    }

    @Nonnull
    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public Type getType() {
        return Type.ACTION;
    }
}
