package ldf.compiler.syntax;

import ldf.compiler.context.ParserContext;
import ldf.compiler.ast.bnf.BnfQuantifier;
import ldf.compiler.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * Syntax validation for quantifiers (which can be found in the BNF syntax).
 *
 * @author Cristian Harja
 */
public final class Check_BnfQuantifier
        extends Inspection<ParserContext, BnfQuantifier> {

    private static final Check_BnfQuantifier
            instance = new Check_BnfQuantifier();

    private Check_BnfQuantifier() {
        super(BnfQuantifier.class);
    }

    public static Check_BnfQuantifier getInstance() {
        return instance;
    }

    @Override
    protected boolean inspect(
            @Nonnull ParserContext ctx,
            @Nonnull BnfQuantifier q
    ) {

        Number min = q.getMin();
        Number max = q.getMax();
        String pat = q.getPattern();

        if (min != null) {
            if (pat.equals("{n}")) {
                if (min.intValue() == 0) {
                    ctx.reportError(q, ctx.i18n().getString(
                            "syntax.quantifier.zero"
                    ));
                } else if(min.intValue() == 1) {
                    ctx.reportWarn(q, ctx.i18n().getString(
                            "syntax.quantifier.redundant"
                    ), "{1}");
                }
            } else if (pat.equals("{n,}")) {
                if (min.intValue() == 0) {
                    ctx.reportWarn(q, ctx.i18n().getString(
                            "syntax.quantifier.can_replace"
                    ), "{0,}", "*");
                } else if (min.intValue() == 1) {
                    ctx.reportWarn(q, ctx.i18n().getString(
                            "syntax.quantifier.can_replace"
                    ), "{1,}", "+");
                }
            } else if (pat.equals("{n,m}") && max != null) {
                int minVal = min.intValue();
                int maxVal = max.intValue();
                if (minVal == 0) {
                    if (maxVal == 1) {
                        ctx.reportWarn(q, ctx.i18n().getString(
                                "syntax.quantifier.can_replace"
                        ), "{0,1}", "?");
                    } else {
                        ctx.reportWarn(q, ctx.i18n().getString(
                                "syntax.quantifier.can_replace"
                        ), "{0,m}", "{,m}");
                    }
                } else if (minVal == maxVal) {
                    if (minVal == 1) {
                        ctx.reportWarn(q, ctx.i18n().getString(
                                "syntax.quantifier.redundant"
                        ), "{1,1}");
                    } else {
                        ctx.reportWarn(q, ctx.i18n().getString(
                                "syntax.quantifier.can_replace"
                        ), "{n,n}", "{n}");
                    }
                } else if (minVal > maxVal) {
                    ctx.reportError(q, ctx.i18n().getString(
                            "syntax.quantifier.n_gt_m"
                    ));
                }
            }
        } else if (max != null) {
            if (pat.equals("{,n}")) {
                if (max.intValue() == 0) {
                    ctx.reportError(q, ctx.i18n().getString(
                            "syntax.quantifier.m_is_0"
                    ));
                } else if (max.intValue() == 1) {
                    ctx.reportWarn(q, ctx.i18n().getString(
                            "syntax.quantifier.can_replace"
                    ), "{,1}", "?");
                }
            }
        }

        return true;
    }

}
