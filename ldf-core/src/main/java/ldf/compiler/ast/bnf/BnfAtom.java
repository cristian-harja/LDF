package ldf.compiler.ast.bnf;

import ldf.compiler.semantics.ags.AgsNode;

/**
 * <p>Common interface for BNF "atoms", which are the kernel of {@link
 * BnfItem BnfItem}s. These are, in turn, the building blocks of BNF
 * expressions.</p>
 *
 * <p>An atom can be one of several possibilities:
 * <ul>
 * <li>a  <b>{@link ldf.compiler.ast.expr.LiteralString string literal}</b>
 * </li>
 * <li>a  <b>{@link ldf.compiler.ast.Reference reference}</b> like a
 *        non-terminal or a fully qualified name.
 * </li>
 * <li>an <b>{@link BnfAlternation alternation}</b>
 * </li>
 * <li>a  <b>{@link BnfAction grammar action}</b> (or
 *        <b>{@link BnfGuard guard}</b>, or a
 *        <b>{@link BnfPlaceholder placeholder}</b>)
 * </li>
 * <li>a (paranthesized) BNF sub-expression</li>
 * </ul></p>
 *
 * <p>The actual type of the object can be distinguished via the {@link
 * #getBnfAtomType} method.</p>
 *
 * @see BnfItem
 * @see BnfAtomType
 *
 * @author Cristian Harja
 */
public interface BnfAtom {
    /**
     * Returns a value indicating the actual type of this atom object.
     */
    BnfAtomType getBnfAtomType();

    /**
     * Returns an `Abstract Grammar Specification` node, equivalent to
     * this node's AST.
     *
     * @see ldf.compiler.semantics.ags.AgsNode
     */
    AgsNode getAbstractGrammarSpec();
}
