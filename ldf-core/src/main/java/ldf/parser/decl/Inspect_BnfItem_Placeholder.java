package ldf.parser.decl;

import ldf.parser.ast.bnf.BnfPlaceholder;
import ldf.parser.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public class Inspect_BnfItem_Placeholder
        extends Inspection<Object, BnfPlaceholder> {
    @Override
    protected boolean inspect(Object ctx, @Nonnull BnfPlaceholder obj) {
        obj.getNearestScope().referenceSymbol(
                obj.getId(), SymbolType.PLACEHOLDER.bitMask
        );

        return true;
    }

    private Inspect_BnfItem_Placeholder() {
        super(BnfPlaceholder.class);
    }

    private static final Inspect_BnfItem_Placeholder
            instance = new Inspect_BnfItem_Placeholder();

    public static Inspect_BnfItem_Placeholder getInstance() {
        return instance;
    }
}
