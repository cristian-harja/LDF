package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.AstNode;
import ldf.parser.ast.type.TypeExpression;
import ldf.parser.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * AST node for a variable being declared. Not a declaration by itself,
 * but used as part of a list. Backed by the {@code decl_var__}
 * non-terminal.
 *
 * @see ldf.parser.ast.stmt.StmtDeclLocalVars
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclVariable extends AstNode {

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
}
