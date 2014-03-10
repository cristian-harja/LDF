package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * @author Cristian Harja
 */
@Immutable
public class DeclFunction extends Declaration {

    @Nonnull
    private String name;

    @Nonnull
    private ExprList args;

    @Nullable
    private TypeExpression type;

    @Nonnull
    private Statement body;

    public DeclFunction(
            @Nonnull String name,
            @Nonnull ExprList args,
            @Nullable TypeExpression type,
            @Nonnull Statement body) {
        this.name = name;
        this.args = args;
        this.type = type;
        this.body = body;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public ExprList getArgs() {
        return args;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }

    @Nonnull
    public Statement getBody() {
        return body;
    }

}
