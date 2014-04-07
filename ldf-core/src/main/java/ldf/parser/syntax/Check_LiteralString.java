package ldf.parser.syntax;

import ldf.parser.Context;
import ldf.parser.ast.expr.LiteralString;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;

/**
 * Syntax validation: report illegal escape sequences ({@link
 * ldf.parser.ast.expr.LiteralString.Fragment} objects marked as invalid).
 *
 * @author Cristian Harja
 */
public final class Check_LiteralString
        extends Inspection<Context, LiteralString> {

    private Check_LiteralString() {
        super(LiteralString.class);
    }

    private static final Check_LiteralString
        instance = new Check_LiteralString();

    public static Check_LiteralString getInstance() {
        return instance;
    }

    @Override
    protected boolean filter(Context ctx, @Nonnull LiteralString obj) {
        return !obj.getInvalidFragments().isEmpty();
    }

    @Override
    protected boolean inspect(Context ctx, @Nonnull LiteralString obj) {
        boolean ran = false;
        for(LiteralString.Fragment f: obj.getInvalidFragments()) {
            Result r = new Result();
            r.fileName = ctx.getFilename();
            r.type = Result.Type.ERROR;
            r.pos = f.getSymbol();
            r.msg = "Illegal escape sequence";
            ctx.report(r);
            ran = true;
        }
        return ran;
    }
}
