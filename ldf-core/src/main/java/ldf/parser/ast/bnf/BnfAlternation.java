package ldf.parser.ast.bnf;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * <p>BNF "alternations" (repetitions of <b>{@code e1}</b>, with <b>{@code
 * e2}</b> in between every two consecutive occurrences of <b>{@code
 * e1}</b>, where {@code e1} and {@code e2} can be any grammar symbol).</p>
 *
 * <p>Syntax (counting occurrences of {@code e1}): <ul>
 * <li>{@code [e1, e2]+}     - {@literal 1} or more</li>
 * <li>{@code [e1, e2]*}     - {@literal 0} or more</li>
 * <li>{@code [e1, e2]{n}}   - exactly {@code n}</li>
 * <li>{@code [e1, e2]{n,}}  - {@code n} or more</li>
 * <li>{@code [e1, e2]{n,m}} - no less than {@code n},
 *                             no more than {@code m}</li>
 * <li>{@code [e1, e2]{,m}}  - less than  {@code n}</li>
 * </ul></p>
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfAlternation implements BnfAtom {

    @Nonnull
    private BnfUnion elementSyntax;

    @Nonnull
    private BnfUnion separatorSyntax;

    /**
     * @param elementSyntax syntax of the element being repeated
     * @param separatorSyntax syntax of the separator symbol
     */
    public BnfAlternation(
            @Nonnull BnfUnion elementSyntax,
            @Nonnull BnfUnion separatorSyntax
    ) {
        this.elementSyntax = elementSyntax;
        this.separatorSyntax = separatorSyntax;
    }

    /**
     * Returns the syntax of the element being repeated
     */
    @Nonnull
    public BnfUnion getElementSyntax() {
        return elementSyntax;
    }

    /**
     * Returns the syntax of the separator
     */
    @Nonnull
    public BnfUnion getSeparatorSyntax() {
        return separatorSyntax;
    }

    /**
     * @return {@link BnfAtomType#ALTERNATION}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.ALTERNATION;
    }
}