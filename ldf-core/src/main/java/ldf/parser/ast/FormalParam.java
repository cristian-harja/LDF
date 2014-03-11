package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * @author Cristian Harja
 */
@Immutable
public class FormalParam {
    @Nonnull
    private String identifier;

    @Nullable
    private TypeExpression type;

    public FormalParam(
            @Nonnull String identifier,
            @Nullable TypeExpression type
    ) {
        this.identifier = identifier;
        this.type = type;
    }

    @Nonnull
    public String getIdentifier() {
        return identifier;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }
}
