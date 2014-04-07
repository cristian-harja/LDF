package ldf.parser.syntax;

import ldf.parser.Context;
import ldf.parser.ast.bnf.BnfAbstractAction;
import ldf.parser.ast.bnf.BnfItem;
import ldf.parser.ast.bnf.BnfLabel;
import ldf.parser.ast.bnf.BnfQuantifier;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;

/**
 * Syntax validation: a grammar action can't have a label or an attached
 * quantifier.
 *
 * @author Cristian Harja
 */
public final class Check_BnfAbstractAction
        extends Inspection<Context, BnfItem> {

    private static final Check_BnfAbstractAction
            instance = new Check_BnfAbstractAction();

    protected Check_BnfAbstractAction() {
        super(BnfItem.class);
    }

    public static Check_BnfAbstractAction getInstance() {
        return instance;
    }

    @Override
    protected boolean filter(Context ctx, @Nonnull BnfItem obj) {
        return obj.getAtom() instanceof BnfAbstractAction;
    }

    @Override
    protected boolean inspect(
            @Nonnull Context ctx,
            @Nonnull BnfItem item
    ) {
        BnfQuantifier quantifier = item.getQuantifier();
        if (quantifier != null) {
            Result res = new Result();
            res.fileName = ctx.getFilename();
            res.pos = quantifier.getSymbol();
            res.type = Result.Type.ERROR;
            res.msg = "Syntax error: " +
                    "Quantifier on grammar action is not allowed";
            ctx.report(res);
        }
        BnfLabel label = item.getLabel();
        if (label != null) {
            Result res = new Result();
            res.fileName = ctx.getFilename();
            res.pos = label.getSymbol();
            res.type = Result.Type.ERROR;
            res.msg = "Syntax error: " +
                    "Label on grammar action is not allowed";
            ctx.report(res);
        }
        return true;
    }
}
