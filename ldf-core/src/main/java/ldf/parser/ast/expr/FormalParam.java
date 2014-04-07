package ldf.parser.ast.expr;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.TypeExpression;
import ldf.parser.ast.decl.Declaration;
import ldf.parser.decl.SymbolType;

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
public final class FormalParam extends Declaration {

    @Nonnull
    private AstIdentifier identifier;

    @Nullable
    private TypeExpression type;

    public FormalParam(
            @Nonnull AstIdentifier name,
            @Nullable TypeExpression type
    ) {
        this.identifier = name;
        this.type = type;
        addAstChildren(name, type);
    }

    @Nonnull
    public AstIdentifier getId() {
        return identifier;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }

    @Nonnull
    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    /**
     * @return {@link SymbolType#FUNCTION_FORMAL_PARAM}
     */
    @Nonnull
    @Override
    public SymbolType getDeclaredSymbolType() {
        return SymbolType.FUNCTION_FORMAL_PARAM;
    }
}
