package ldf.parser.ast.bnf;

import ldf.parser.ast.Reference;
import ldf.parser.ast.expr.LiteralString;

/**
 * Used to distinguish between different classes implementing the {@link
 * BnfAtom} interface.
 *
 * @see BnfAtom#getBnfAtomType()
 * @author Cristian Harja
 */
public enum BnfAtomType {

    /**
     * @see Reference
     */
    REFERENCE,

    /**
     * @see LiteralString
     */
    LITERAL_CHAR,

    /**
     * @see LiteralString
     */
    LITERAL_STRING,

    /**
     * @see BnfAlternation
     */
    ALTERNATION,

    /**
     * @see BnfPlaceholder
     */
    PLACEHOLDER,

    /**
     * @see BnfAction
     */
    ACTION,

    /**
     * @see BnfGuard
     */
    GUARD,

    /**
     * @see BnfItem
     */
    ITEM,

    /**
     * @see BnfConcat
     */
    CONCATENATION,

    /**
     * @see BnfUnion
     */
    UNION

}
