package ldf.parser.decl;

import ldf.parser.Context;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Cristian Harja
 */
public class Inspect_SymbolUsage
        extends Inspection<Context, Scope>{

    @Override
    protected boolean inspect(Context ctx, @Nonnull Scope obj) {

        // report unused symbols
        for (Collection<SymbolDef> defs : obj.getSymbolDefs().values()) {
            for (SymbolDef sym : defs) {
                if (sym.getType() == SymbolType.NTERM ||
                    sym.getType() == SymbolType.GRAMMAR) {
                    continue;
                }
                if (sym.getBackReferences().size() == 0) {
                    if (sym.getName().equals("_")) {
                        continue;
                    }
                    Result result = new Result();
                    result.type = Result.Type.WARN;
                    result.fileName = ctx.getFilename();
                    result.pos = sym.getId();
                    result.msg = "Unused symbol: " + sym.getName();
                    ctx.report(result);
                }
            }
        }

        // report unresolved symbols
        for (Collection<SymbolRef> refs : obj.getSymbolRefs().values()) {
            for (SymbolRef sym : refs) {
                if (sym.getName().equals("INT") ||
                    sym.getName().equals("ID") ||
                    sym.getName().equals("__external")
                ) {
                    continue;
                }
                if (sym.resolve() == null) {
                    Result result = new Result();
                    result.type = Result.Type.ERROR;
                    result.fileName = ctx.getFilename();
                    result.pos = sym.getId();
                    if (sym.getCandidates().size() == 0) {
                        result.msg = "Symbol not found: " + sym.getName();
                    } else {
                        result.msg = "Ambiguous reference: " + sym.getName();
                    }
                    ctx.report(result);
                }
            }
        }

        return true;
    }

    private Inspect_SymbolUsage() {
        super(Scope.class);
    }

    private static final Inspect_SymbolUsage
            instance = new Inspect_SymbolUsage();

    public static Inspect_SymbolUsage getInstance() {
        return instance;
    }
}
