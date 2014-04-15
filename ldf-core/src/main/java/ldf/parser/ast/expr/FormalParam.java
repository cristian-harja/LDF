package ldf.parser.ast.expr;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.AstNode;
import ldf.parser.ast.TypeExpression;

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
