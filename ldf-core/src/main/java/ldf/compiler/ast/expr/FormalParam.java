package ldf.compiler.ast.expr;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.type.TypeExpression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Formal parameter.
 *
 * @see FormalParamList
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class FormalParam extends AstNode {

    @Nonnull
    private AstIdentifier id;

    @Nullable
    private TypeExpression type;

    public FormalParam(
            @Nonnull AstIdentifier name,
            @Nullable TypeExpression type
    ) {
        this.id = name;
        this.type = type;
        addAstChildren(name, type);
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }
}
