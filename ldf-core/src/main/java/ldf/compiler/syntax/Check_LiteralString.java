package ldf.compiler.syntax;

import ldf.compiler.ParserContext;
import ldf.compiler.ast.expr.LiteralString;
import ldf.compiler.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * Syntax validation: report illegal escape sequences ({@link
 * ldf.compiler.ast.expr.LiteralString.Fragment} objects marked as invalid).
 *
 * @author Cristian Harja
 */
public final class Check_LiteralString
        extends Inspection<ParserContext, LiteralString> {

    private Check_LiteralString() {
        super(LiteralString.class);
    }

    private static final Check_LiteralString
        instance = new Check_LiteralString();

    public static Check_LiteralString getInstance() {
        return instance;
    }

    @Override
    protected boolean filter(ParserContext ctx, @Nonnull LiteralString obj) {
        return !obj.getInvalidFragments().isEmpty();
    }

    @Override
    protected boolean inspect(ParserContext ctx, @Nonnull LiteralString obj) {
        boolean ran = false;
        for(LiteralString.Fragment f: obj.getInvalidFragments()) {
            ctx.reportError(f, ctx.i18n().getString(
                    "syntax.string.illegal_escape_seq"
            ));
            ran = true;
        }
        return ran;
    }
}
