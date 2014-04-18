package ldf.parser.ast.bnf;

import ldf.parser.ags.AgsNode;
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
    private BnfUnion astRoot;

    @Nonnull
    private AgsNode agsRoot;

    /**
     * @param root the BNF expression
     */
    public BnfSyntax(@Nonnull BnfUnion root) {
        astRoot = root;
        addAstChildren(root);
        agsRoot = astRoot.getAbstractGrammarSpec();
    }

    /**
     * @return the BNF expression
     */
    @Nonnull
    public BnfUnion getAstRoot() {
        return astRoot;
    }

    @Nonnull
    public AgsNode getAgsRoot() {
        return agsRoot;
    }
}
