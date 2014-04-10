package ldf.parser.syntax;

import ldf.parser.Context;
import ldf.parser.ast.bnf.BnfQuantifier;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;

/**
 * Syntax validation for quantifiers (which can be found in the BNF syntax).
 *
 * @author Cristian Harja
 */
public final class Check_BnfQuantifier
        extends Inspection<Context, BnfQuantifier> {

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
            @Nonnull Context ctx,
            @Nonnull BnfQuantifier q
    ) {
        Result res = new Result();
        res.fileName = ctx.getFilename();
        res.type = Result.Type.WARN; // most of them are warnings
        res.pos = q;

        Number min = q.getMin();
        Number max = q.getMax();
        String pat = q.getPattern();

        if (min != null) {
            if (pat.equals("{n}")) {
                if (min.intValue() == 0) {
                    res.type = Result.Type.ERROR;
                    res.msg = "A quantity of zero is not allowed";
                } else if(min.intValue() == 1) {
                    res.msg = "Redundant: {1} can be omitted";
                }
            } else if (pat.equals("{n,}")) {
                if (min.intValue() == 0) {
                    res.msg = "Redundant: {0,} can be replaced with `*`";
                } else if (min.intValue() == 1) {
                    res.msg = "Redundant: {1,} can be replaced with `+`";
                }
            } else if (pat.equals("{n,m}") && max != null) {
                int minVal = min.intValue();
                int maxVal = max.intValue();
                if (minVal == 0) {
                    if (maxVal == 1) {
                        res.msg = "Redundant: {0,1} can be replaced with `?`";
                    } else {
                        res.msg = "Redundant: {0,m} can be replaced with {,m}";
                    }
                } else if (minVal == maxVal) {
                    if (minVal == 1) {
                        res.msg = "Redundant: {1,1} can be omitted";
                    } else {
                        res.msg = "Redundant: {n,n} can be replaced with {n}";
                    }
                } else if (minVal > maxVal) {
                    res.type = Result.Type.ERROR;
                    res.msg = "Lower bound greater than upper bound";
                }
            }
        } else if (max != null) {
            if (pat.equals("{,n}")) {
                if (max.intValue() == 0) {
                    res.type = Result.Type.ERROR;
                    res.msg = "An upper bound of zero is not allowed";
                } else if (max.intValue() == 1) {
                    res.msg = "Redundant: {,1} can be replaced with `?`";
                }
            }
        }

        if (res.msg != null) {
            ctx.report(res);
        }
        return true;
    }

}
