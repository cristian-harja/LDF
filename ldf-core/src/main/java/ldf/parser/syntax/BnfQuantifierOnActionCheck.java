package ldf.parser.syntax;

import ldf.parser.Context;
import ldf.parser.ast.AstNode;
import ldf.parser.ast.bnf.BnfAbstractAction;
import ldf.parser.ast.bnf.BnfItem;
import ldf.parser.ast.bnf.BnfQuantifier;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;

/**
 * Syntax validation: a grammar action can't have an attached quantifier.
 *
 * @author Cristian Harja
 */
public final class BnfQuantifierOnActionCheck
        extends Inspection<BnfAbstractAction> {

    private static BnfQuantifierOnActionCheck
            instance = new BnfQuantifierOnActionCheck();

    protected BnfQuantifierOnActionCheck() {
        super(BnfAbstractAction.class);
    }

    public static BnfQuantifierOnActionCheck getInstance() {
        return instance;
    }

    @Override
    protected boolean filter(@Nonnull BnfAbstractAction obj) {
        AstNode parent = obj.getAstParent();
        return parent != null && parent instanceof BnfItem;
    }

    @Override
    protected boolean inspect(
            @Nonnull Context ctx,
            @Nonnull BnfAbstractAction obj
    ) {
        BnfItem item = (BnfItem) obj.getAstParent();
        BnfQuantifier quantifier = item.getQuantifier();
        if (quantifier == null) return false;
        Result res = new Result();
        res.fileName = ctx.getFilename();
        res.pos = quantifier.getSymbol();
        res.type = Result.Type.ERROR;
        res.msg = "Quantifier on grammar action is not allowed";
        ctx.report(res);
        return true;
    }
}
