package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Cristian Harja
 */
public final class DeclNonTerminal extends Declaration {
    @Nonnull
    private final String name;

    @Nonnull
    private BnfSyntax productions;

    @Nullable
    private DeclWhereClause whereClause;

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
