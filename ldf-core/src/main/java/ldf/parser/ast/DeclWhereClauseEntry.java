package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * @author Cristian Harja
 */
@Immutable
public final class DeclWhereClauseEntry {
    @Nonnull
    private String identifier;

    @Nonnull
    private BnfAbstractAction action;

    public DeclWhereClauseEntry(
            @Nonnull String identifier,
            @Nonnull BnfAbstractAction action
    ) {
        this.identifier = identifier;
        this.action = action;
    }

    @Nonnull
    public String getIdentifier() {
        return identifier;
    }

    @Nonnull
    public BnfAbstractAction getAction() {
        return action;
    }
}
