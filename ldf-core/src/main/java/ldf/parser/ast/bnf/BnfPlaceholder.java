package ldf.parser.ast.bnf;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.decl.Declaration;
import ldf.parser.decl.SymbolType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * An item in the BNF syntax which expands to a grammar action (in a
 * {@code where} clause).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class BnfPlaceholder extends Declaration
        implements BnfAbstractAction{

    @Nonnull
    private AstIdentifier id;

    /**
     * @param label the name of this placeholder
     */
    public BnfPlaceholder(@Nonnull AstIdentifier label) {
        this.id = label;
        addAstChildren(label);
    }

    /**
     * @return name of this placeholder
     */
    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    /**
     * @return {@link BnfAtomType#PLACEHOLDER}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.PLACEHOLDER;
    }

    @Nonnull
    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    /**
     * @return {@link SymbolType#PLACEHOLDER}
     */
    @Nonnull
    @Override
    public SymbolType getDeclaredSymbolType() {
        return SymbolType.PLACEHOLDER;
    }
}
