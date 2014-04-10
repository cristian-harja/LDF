package ldf.parser.decl;

import ldf.parser.ast.bnf.BnfSyntax;
import ldf.parser.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public class Inspect_BnfSyntax
        extends Inspection<Object, BnfSyntax> {
    @Override
    protected boolean inspect(Object ctx, @Nonnull BnfSyntax obj) {
        obj.getDag(); // builds the directed acyclic graph
        return true;
    }

    private Inspect_BnfSyntax() {
        super(BnfSyntax.class);
    }

    private static final Inspect_BnfSyntax
            instance = new Inspect_BnfSyntax();

    public static Inspect_BnfSyntax getInstance() {
        return instance;
    }
}
