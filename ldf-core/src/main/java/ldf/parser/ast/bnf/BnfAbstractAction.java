package ldf.parser.ast.bnf;

import ldf.parser.ast.AstNode;

/**
 * A common interface for: {@link BnfAction}, {@link BnfGuard} and {@link
 * BnfPlaceholder}. Backed by the {@code bnf_action} non-terminal.
 *
 * @author Cristian Harja
 */
public abstract class BnfAbstractAction extends AstNode
        implements BnfAtom {
}
