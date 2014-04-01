package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.bnf.BnfSyntax;

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
    private AstIdentifier name;

    @Nonnull
    private BnfSyntax syntax;

    @Nullable
    private DeclWhereClause whereClause;

    /**
     * @param name name of the non-terminal
     * @param syntax right-hand side of the declaration
     * @param whereClause optional {@code where} clause
     */
    public DeclNonTerminal(
            @Nonnull AstIdentifier name,
            @Nonnull BnfSyntax syntax,
            @Nullable DeclWhereClause whereClause
    ) {
        this.name = name;
        this.syntax = syntax;
        this.whereClause = whereClause;
        addAstChildren(name, syntax, whereClause);
    }

    @Nonnull
    public BnfSyntax getSyntax() {
        return syntax;
    }

    @Nullable
    public DeclWhereClause getWhereClause() {
        return whereClause;
    }

    @Nonnull
    public AstIdentifier getName() {
        return name;
    }
}
