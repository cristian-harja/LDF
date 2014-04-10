package ldf.parser.decl;

import ldf.parser.ast.expr.FormalParam;
import ldf.parser.ast.expr.FormalParamList;
import ldf.parser.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public class Inspect_FormalParamList
        extends Inspection<Object, FormalParamList> {

    @Override
    protected boolean inspect(Object ctx, @Nonnull FormalParamList obj) {
        Scope s = obj.getNearestScope();

        for (FormalParam param: obj.getParameterList()) {
            s.defineSymbol(
                    param.getId(),
                    SymbolType.VARIABLE
            );
        }
        return true;
    }

    private Inspect_FormalParamList() {
        super(FormalParamList.class);
    }

    private static final Inspect_FormalParamList
            instance = new Inspect_FormalParamList();

    public static Inspect_FormalParamList getInstance() {
        return instance;
    }
}
