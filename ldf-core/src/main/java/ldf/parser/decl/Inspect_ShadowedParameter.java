package ldf.parser.decl;

import ldf.parser.Context;
import ldf.parser.ast.decl.DeclFunction;
import ldf.parser.inspect.Inspection;
import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Cristian Harja
 */
public class Inspect_ShadowedParameter
        extends Inspection<Context, DeclFunction>{

    @Override
    protected boolean inspect(Context ctx, @Nonnull DeclFunction obj) {
        Map<String, Collection<SymbolDef>> args;
        Map<String, Collection<SymbolDef>> vars;
        Set<String> var_set;
        Set<String> arg_set;

        args = obj.getArgs().getOwnScope().getSymbolDefs();
        vars = obj.getBody().getOwnScope().getSymbolDefs();
        arg_set = args.keySet();
        var_set = vars.keySet();

        for (String k: var_set) {
            if (arg_set.contains(k)) {
                Result res = new Result();
                res.type = Result.Type.WARN;
                res.pos = vars.get(k).iterator().next().getId();
                res.fileName = ctx.getFilename();
                res.msg = "Variable declaration shadows function parameter";
                ctx.report(res);
            }
        }

        return false;
    }

    private Inspect_ShadowedParameter() {
        super(DeclFunction.class);
    }

    private static final Inspect_ShadowedParameter
            instance = new Inspect_ShadowedParameter();

    public static Inspect_ShadowedParameter getInstance() {
        return instance;
    }
}
