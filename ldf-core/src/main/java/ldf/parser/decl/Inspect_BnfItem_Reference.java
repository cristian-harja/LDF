package ldf.parser.decl;

import ldf.parser.ast.Reference;
import ldf.parser.ast.bnf.BnfItem;
import ldf.parser.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public class Inspect_BnfItem_Reference
        extends Inspection<Object, Reference> {

    @Override
    protected boolean filter(Object ctx, @Nonnull Reference obj) {
        return obj.getAstParent() instanceof BnfItem;
    }

    @Override
    protected boolean inspect(Object ctx, @Nonnull Reference obj) {
        if (obj.getPath().get(0).getName().equals("_")) {
            return false;
        }
        obj.getNearestScope().referenceSymbol(
                obj.getPath().get(0), SymbolType.NTERM.bitMask
        );
        return true;
    }

    private Inspect_BnfItem_Reference() {
        super(Reference.class);
    }

    private static final Inspect_BnfItem_Reference
            instance = new Inspect_BnfItem_Reference();

    public static Inspect_BnfItem_Reference getInstance() {
        return instance;
    }
}
