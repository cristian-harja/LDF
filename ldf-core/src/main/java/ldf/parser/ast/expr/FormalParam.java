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
    private AstIdentifier identifier;

    @Nullable
    private TypeExpression type;

    public FormalParam(
            @Nonnull AstIdentifier identifier,
            @Nullable TypeExpression type
    ) {
        this.identifier = identifier;
        this.type = type;
        addAstChildren(identifier, type);
    }

    @Nonnull
    public AstIdentifier getIdentifier() {
        return identifier;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }
}
