package ldf.compiler.syntax;

import ldf.compiler.context.ParserContext;
import ldf.compiler.ast.bnf.BnfAbstractAction;
import ldf.compiler.ast.bnf.BnfItem;
import ldf.compiler.ast.bnf.BnfLabel;
import ldf.compiler.ast.bnf.BnfQuantifier;
import ldf.compiler.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * Syntax validation: a grammar action can't have a label or an attached
 * quantifier.
 *
 * @author Cristian Harja
 */
public final class Check_BnfAbstractAction
        extends Inspection<ParserContext, BnfItem> {

    private static final Check_BnfAbstractAction
            instance = new Check_BnfAbstractAction();

    protected Check_BnfAbstractAction() {
        super(BnfItem.class);
    }

    public static Check_BnfAbstractAction getInstance() {
        return instance;
    }

    @Override
    protected boolean filter(ParserContext ctx, @Nonnull BnfItem obj) {
        return obj.getAtom() instanceof BnfAbstractAction;
    }

    @Override
    protected boolean inspect(
            @Nonnull ParserContext ctx,
            @Nonnull BnfItem item
    ) {
        BnfQuantifier quantifier = item.getQuantifier();
        if (quantifier != null) {
            ctx.reportError(quantifier, ctx.i18n().getString(
                    "syntax.action.has_quantifier"
            ));
        }
        BnfLabel label = item.getLabel();
        if (label != null) {
            ctx.reportError(label, ctx.i18n().getString(
                    "syntax.action.has_label"
            ));
        }
        return true;
    }
}
