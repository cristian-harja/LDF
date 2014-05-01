package ldf.compiler.ast;

import ldf.compiler.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * AST node for an identifier.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class AstIdentifier extends Expression {
    @Nonnull
    private String name;

    public AstIdentifier(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
