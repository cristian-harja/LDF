package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.TypeExpression;
import ldf.parser.ast.expr.FormalParamList;
import ldf.parser.ast.stmt.StmtBlock;
import ldf.parser.decl.SymbolType;

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
    private AstIdentifier identifier;

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
        this.identifier = name;
        this.args = args;
        this.type = type;
        this.body = body;
        addAstChildren(name, args, type, body);
    }

    @Nonnull
    public AstIdentifier getId() {
        return identifier;
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

    @Override
    public boolean hasOwnScope() {
        return true;
    }

    @Nonnull
    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    /**
     * @return {@link SymbolType#FUNCTION}
     */
    @Nonnull
    @Override
    public SymbolType getDeclaredSymbolType() {
        return SymbolType.FUNCTION;
    }

    /**
     * Overrides {@link ldf.parser.ast.AstNode#initScopes()
     * AstNode.initScopes} in order to set the parent scope of the
     * function body to be the scope of the formal parameters. This way
     * the parameters become visible to the function body (even though,
     * hierarchically, they are not above the function body in the AST).
     */
    @Override
    public synchronized void initScopes() {
        super.initScopes();
        body.getOwnScope().setParentScope(args.getOwnScope());
    }
}
