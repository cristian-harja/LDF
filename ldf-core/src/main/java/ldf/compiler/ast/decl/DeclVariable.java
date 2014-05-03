package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.expr.Expression;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * AST node for a variable being declared. Backed by the {@code decl_var__}
 * non-terminal.
 *
 * @see ldf.compiler.ast.stmt.StmtDeclLocalVars
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclVariable extends Declaration {

    @Nonnull
    private AstIdentifier id;

    @Nullable
    private TypeExpression type;

    @Nullable
    private Expression initializer;

    /**
     * @param name variable name
     * @param type optional variable type
     * @param initializer optional variable initializer
     */
    public DeclVariable(
            @Nonnull AstIdentifier name,
            @Nullable TypeExpression type,
            @Nullable Expression initializer
    ) {
        this.id = name;
        this.type = type;
        this.initializer = initializer;
        addAstChildren(name, type, initializer);
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }

    @Nullable
    public Expression getInitializer() {
        return initializer;
    }

    @Nullable
    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    @Nullable
    @Override
    public NsNodeType getDeclaredSymbolType() {
        return NsNodeType.VARIABLE;
    }
}
