package ldf.parser.ast.bnf;

import ldf.parser.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import static ldf.parser.ast.bnf.BnfSyntaxDag.DagHandle;

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

    private DagHandle dag;

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

    @Nonnull
    public DagHandle getDag() {
        if (dag == null) {
            synchronized (this) {
                if (dag == null) {
                    dag = BnfSyntaxDag.buildDag(this);
                }
            }
        }
        return dag;
    }
}
