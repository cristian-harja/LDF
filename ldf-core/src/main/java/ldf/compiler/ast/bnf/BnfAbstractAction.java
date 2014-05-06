package ldf.compiler.ast.bnf;

import ldf.compiler.semantics.ags.AgsNode;
import ldf.compiler.ast.AstNode;

import javax.annotation.Nonnull;

/**
 * A common interface for: {@link BnfAction}, {@link BnfGuard} and {@link
 * BnfPlaceholder}. Backed by the {@code bnf_action} non-terminal.
 *
 * @author Cristian Harja
 */
public abstract class BnfAbstractAction extends AstNode
        implements BnfAtom {

    protected AgsNode agsNode;

    public BnfAbstractAction() {
        agsNode = AgsNode.agsInit(this);
    }

    @Nonnull
    @Override
    public AgsNode getAbstractGrammarSpec() {
        return agsNode;
    }
}
