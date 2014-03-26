package ldf.parser.ast.bnf;

import javax.annotation.concurrent.Immutable;

/**
 * <p>Common interface for BNF "atoms", which are the kernel of {@link
 * BnfItem BnfItem}s. These are, in turn, the building blocks of BNF
 * expressions.</p>
 *
 * <p>An atom can be one of several possibilities:
 * <ul>
 * <li>a  <b>{@link ldf.parser.ast.expr.LiteralString string literal}</b>
 * </li>
 * <li>a  <b>{@link ldf.parser.ast.expr.ExprReference reference}</b> (ex:
 *        identifier)
 * </li>
 * <li>an <b>{@link ldf.parser.ast.bnf.BnfAlternation alternation}</b>
 * </li>
 * <li>a  <b>{@link ldf.parser.ast.bnf.BnfAction grammar action}</b> (or
 *        <b>{@link ldf.parser.ast.bnf.BnfGuard guard}</b>, or a
 *        <b>{@link ldf.parser.ast.bnf.BnfPlaceholder placeholder}</b>)
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
@Immutable
public interface BnfAtom {
    /**
     * Returns a value indicating the actual type of this atom object.
     */
    BnfAtomType getBnfAtomType();
}
