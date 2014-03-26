package ldf.parser.ast.decl;

import ldf.parser.ast.bnf.BnfSyntax;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Declaration of a <em>non-terminal</em>.
 * Example {@code nterm MyNterm ::= ... }.
 * Backed by the {@code decl_nterm} non-terminal.
 *
 * @author Cristian Harja
 */
@Immutable
public final class DeclNonTerminal implements Declaration {
    @Nonnull
    private final String name;

    @Nonnull
    private BnfSyntax productions;

    @Nullable
    private DeclWhereClause whereClause;

    /**
     * @param name name of the non-terminal
     * @param productions right-hand side of the declaration
     * @param whereClause optional {@code where} clause
     */
    public DeclNonTerminal(
            @Nonnull String name,
            @Nonnull BnfSyntax productions,
            @Nullable DeclWhereClause whereClause
    ) {
        this.name = name;
        this.productions = productions;
        this.whereClause = whereClause;
    }

    @Nonnull
    public BnfSyntax getProductions() {
        return productions;
    }

    @Nullable
    public DeclWhereClause getWhereClause() {
        return whereClause;
    }

    @Nonnull
    public String getName() {
        return name;
    }
}
