package ldf.parser.ast.decl;

import ldf.parser.ast.TypeExpression;
import ldf.parser.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * AST node for a variable being declared. Not a declaration by itself,
 * but used as part of a list (see {@link DeclVars}). Backed by the
 * {@code decl_var__} non-terminal.
 *
 * @author Cristian Harja
 */
@Immutable
public final class DeclVariable {

    @Nonnull
    private String name;

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
            @Nonnull String name,
            @Nullable TypeExpression type,
            @Nullable Expression initializer
    ) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }

    @Nonnull
    public String getName() {
        return name;
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
