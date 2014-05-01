package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.ast.expr.FormalParamList;
import ldf.compiler.ast.stmt.StmtBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Function declaration. Backed by the {@code decl_function} non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclFunction extends Declaration {

    @Nonnull
    private AstIdentifier id;

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
            @Nonnull AstIdentifier name,
            @Nonnull FormalParamList args,
            @Nullable TypeExpression type,
            @Nonnull StmtBlock body) {
        this.id = name;
        this.args = args;
        this.type = type;
        this.body = body;
        addAstChildren(name, args, type, body);
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
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
