package ldf.parser.decl;

import ldf.parser.Context;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;
import java.util.Collection;

import static ldf.parser.decl.SymbolType.*;

/**
 * @author Cristian Harja
 */
public class Inspect_DuplicateDeclaration
        extends Inspection<Context, Scope> {
    @Override
    protected boolean inspect(Context ctx, @Nonnull Scope obj) {
        for (Collection<SymbolDef> defs: obj.getSymbolDefs().values()) {
            if ((obj.getBitMask() & NTERM_LABEL.bitMask) == 0
                    && defs.size() > 1
            ) {
                String msg = null;
                for (SymbolDef sym : defs) {
                    if (msg == null) {
                        msg = "Duplicate declaration: " + sym.getName();
                    }
                    Result res = new Result();
                    res.type = Result.Type.ERROR;
                    res.fileName = ctx.getFilename();
                    res.pos = sym;
                    res.msg = msg;
                    ctx.report(res);
                }
            }
        }
        return true;
    }

    private Inspect_DuplicateDeclaration() {
        super(Scope.class);
    }

    private static final Inspect_DuplicateDeclaration
            instance = new Inspect_DuplicateDeclaration();

    public static Inspect_DuplicateDeclaration getInstance() {
        return instance;
    }
}
