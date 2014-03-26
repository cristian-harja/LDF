package ldf.parser.ast.decl;

import ldf.parser.ast.TypeExpression;
import ldf.parser.ast.expr.FormalParamList;
import ldf.parser.ast.stmt.StmtBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Function declaration. Backed by the {@code decl_function} non-terminal.
 *
 * @author Cristian Harja
 */
@Immutable
public final class DeclFunction implements Declaration {

    @Nonnull
    private String name;

    @Nonnull
    private FormalParamList args;

    @Nullable
    private TypeExpression type;

    @Nonnull
    private StmtBlock body;

    /**
     * @param name function name
     * @param args function arguments
     * @param type return type
     * @param body function body
     */
    public DeclFunction(
            @Nonnull String name,
            @Nonnull FormalParamList args,
            @Nullable TypeExpression type,
            @Nonnull StmtBlock body) {
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
    public FormalParamList getArgs() {
        return args;
    }

    @Nullable
    public TypeExpression getType() {
        return type;
    }

    @Nonnull
    public StmtBlock getBody() {
        return body;
    }

}
