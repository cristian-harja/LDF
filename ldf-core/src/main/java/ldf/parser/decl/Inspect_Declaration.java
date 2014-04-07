package ldf.parser.decl;

import ldf.parser.ast.decl.Declaration;
import ldf.parser.inspect.Inspection;

import javax.annotation.Nonnull;

/**
 * An inspection which runs over instances of {@link Declaration} and
 * adds the corresponding symbol declarations to their parent scope.
 *
 * @see ldf.parser.ast.decl.DeclFunction
 * @see ldf.parser.ast.decl.DeclGrammar
 * @see ldf.parser.ast.decl.DeclNonTerminal
 * @see ldf.parser.ast.decl.DeclVariable
 *
 * @author Cristian Harja
 */
public class Inspect_Declaration
        extends Inspection<Object, Declaration>{

    @Override
    protected boolean inspect(Object ctx, @Nonnull Declaration obj) {
        Scope s = obj.assertGetAstParent().getNearestScope();
        s.defineSymbol(
                obj.getDeclaredSymbolName(),
                obj.getDeclaredSymbolType()
        );
        return false;
    }

    private Inspect_Declaration() {
        super(Declaration.class);
    }

    private static final Inspect_Declaration
            instance = new Inspect_Declaration();

    public static Inspect_Declaration getInstance() {
        return instance;
    }
}
