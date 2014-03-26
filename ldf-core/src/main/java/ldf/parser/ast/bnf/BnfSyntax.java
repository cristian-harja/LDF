package ldf.parser.ast.bnf;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A wrapper over a BNF expression. Backed by the {@code bnf_syntax}
 * non-terminal.
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfSyntax {
    @Nonnull
    private BnfUnion root;

    /**
     * @param root the BNF expression
     */
    public BnfSyntax(@Nonnull BnfUnion root) {
        this.root = root;
    }

    /**
     * @return the BNF expression
     */
    @Nonnull
    public BnfUnion getRoot() {
        return root;
    }
}
