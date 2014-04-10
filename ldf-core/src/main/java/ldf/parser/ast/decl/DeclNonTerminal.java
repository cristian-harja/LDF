package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.bnf.BnfSyntax;
import ldf.parser.decl.SymbolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import static ldf.parser.decl.SymbolType.*;

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
    private AstIdentifier identifier;

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
            @Nullable BnfSyntax syntax,
            @Nullable DeclWhereClause whereClause
    ) {
        this.identifier = identifier;
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
        return identifier;
    }

    @Override
    public boolean hasOwnScope() {
        return true;
    }

    @Override
    protected int getAcceptedTypes() {
        return NTERM_LABEL.bitMask | PLACEHOLDER.bitMask;
    }

    @Nonnull
    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    /**
     * @return {@link SymbolType#NTERM}
     */
    @Nonnull
    @Override
    public SymbolType getDeclaredSymbolType() {
        return NTERM;
    }
}
