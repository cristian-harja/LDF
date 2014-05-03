package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.stmt.Statement;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nullable;

/**
 * A common interface for declarative statements.
 *
 * @author Cristian Harja
 */
public abstract class Declaration extends Statement {

    @Nullable
    public AstIdentifier getDeclaredSymbolName() {
        return null;
    }

    @Nullable
    public NsNodeType getDeclaredSymbolType() {
        return null;
    }

}
