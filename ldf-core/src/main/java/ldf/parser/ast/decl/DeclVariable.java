package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.TypeExpression;
import ldf.parser.ast.expr.Expression;
import ldf.parser.decl.SymbolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * AST node for a variable being declared. Not a declaration by itself,
 * but used as part of a list (see {@link ldf.parser.ast.stmt.StmtDeclLocalVars}). Backed by the
 * {@code decl_var__} non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclVariable extends Declaration {

    @Nonnull
    private AstIdentifier identifier;

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
        this.identifier = name;
        this.type = type;
        this.initializer = initializer;
        addAstChildren(name, type, initializer);
    }

    @Nonnull
    public AstIdentifier getId() {
        return identifier;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }

    @Nullable
    public Expression getInitializer() {
        return initializer;
    }

    @Nonnull
    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    /**
     * @return {@link SymbolType#VARIABLE}
     */
    @Nonnull
    @Override
    public SymbolType getDeclaredSymbolType() {
        return SymbolType.VARIABLE;
    }
}
