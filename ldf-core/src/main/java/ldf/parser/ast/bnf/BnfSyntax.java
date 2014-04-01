package ldf.parser.ast.bnf;

import ldf.parser.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A wrapper over a BNF expression. Backed by the {@code bnf_syntax}
 * non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class BnfSyntax extends AstNode {
    @Nonnull
    private BnfUnion root;

    /**
     * @param root the BNF expression
     */
    public BnfSyntax(@Nonnull BnfUnion root) {
        this.root = root;
        addAstChildren(root);
    }

    /**
     * @return the BNF expression
     */
    @Nonnull
    public BnfUnion getRoot() {
        return root;
    }
}
