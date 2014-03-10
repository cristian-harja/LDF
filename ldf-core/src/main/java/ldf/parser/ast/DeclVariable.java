package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Cristian Harja
 */
public class DeclVariable extends Declaration{

    @Nonnull
    private String name;

    @Nullable
    private TypeExpression type;

    @Nullable
    private Expression initializer;


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
