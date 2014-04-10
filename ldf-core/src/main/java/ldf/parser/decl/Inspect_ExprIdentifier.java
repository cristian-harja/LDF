package ldf.parser.decl;

import ldf.parser.ast.expr.ExprIdentifier;
import ldf.parser.inspect.Inspection;

import javax.annotation.Nonnull;

import static ldf.parser.decl.SymbolType.*;

/**
 * Populates scopes with references that need to be resolved (instances
 * of {@link SymbolRef}).
 *
 * @author Cristian Harja
 */
public class Inspect_ExprIdentifier
        extends Inspection<Object, ExprIdentifier>{

    @Override
    protected boolean inspect(Object ctx, @Nonnull ExprIdentifier obj) {
        Scope s = obj.getNearestScope();

        s.referenceSymbol(obj.getId(),
                VARIABLE.bitMask |
                NTERM_LABEL.bitMask |
                FUNCTION_FORMAL_PARAM.bitMask
        );
        return true;
    }

    private Inspect_ExprIdentifier() {
        super(ExprIdentifier.class);
    }

    private static final Inspect_ExprIdentifier
            instance = new Inspect_ExprIdentifier();

    public static Inspect_ExprIdentifier getInstance() {
        return instance;
    }
}
