package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.AstNode;
import ldf.parser.decl.SymbolType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A common interface for nodes which declare a symbol. Sub-classes
 * implement the {@link #getDeclaredSymbolName} and {@link
 * #getDeclaredSymbolType}.
 *
 * @see ldf.parser.decl.Inspect_Declaration
 *
 * @author Cristian Harja
 */
@ThreadSafe
public abstract class Declaration extends AstNode {

    /**
     * @return the identifier which introduced the name of the symbol
     *         being declared
     */
    @Nonnull
    public abstract AstIdentifier getDeclaredSymbolName();

    @Nonnull
    public abstract SymbolType    getDeclaredSymbolType();
}
