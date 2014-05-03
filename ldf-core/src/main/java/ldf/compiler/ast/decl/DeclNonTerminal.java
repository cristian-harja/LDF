package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.bnf.BnfSyntax;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Declaration of a <em>non-terminal</em>.
 * Example {@code nterm MyNterm ::= ... }.
 * Backed by the {@code decl_nterm} non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclNonTerminal extends Declaration {
    @Nonnull
    private AstIdentifier id;

    @Nonnull
    private TypeExpression type;

    @Nullable
    private BnfSyntax syntax;

    @Nullable
    private DeclWhereClause whereClause;

    /**
     * @param identifier name of the non-terminal
     * @param syntax right-hand side of the declaration
     * @param whereClause optional {@code where} clause
     */
    public DeclNonTerminal(
            @Nonnull AstIdentifier identifier,
            @Nonnull TypeExpression type,
            @Nullable BnfSyntax syntax,
            @Nullable DeclWhereClause whereClause
    ) {
        this.id = identifier;
        this.type = type;
        this.syntax = syntax;
        this.whereClause = whereClause;
        addAstChildren(identifier, syntax, whereClause);
    }

    @Nullable
    public BnfSyntax getSyntax() {
        return syntax;
    }

    @Nullable
    public DeclWhereClause getWhereClause() {
        return whereClause;
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    @Nonnull
    public TypeExpression getType() {
        return type;
    }

    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    @Override
    public NsNodeType getDeclaredSymbolType() {
        return NsNodeType.NTERM;
    }
}
