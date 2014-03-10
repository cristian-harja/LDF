package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * <p>Syntax: {@code [e1, e2]+} or {@code [e1, e2]*} or {@code [e1, e2]{}},
 * etc. </p>
 *
 * <p>Expresses repetitions of <b>e1</b>, with <b>e2</b> in between every
 * two (consecutive) occurrences of <b>e1</b>.</p>
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfAlternation {
    @Nonnull
    private BnfJoin elementSyntax, separatorSyntax;

    public BnfAlternation(
            @Nonnull BnfJoin elementSyntax,
            @Nonnull BnfJoin separatorSyntax
    ) {
        this.elementSyntax = elementSyntax;
        this.separatorSyntax = separatorSyntax;
    }

    @Nonnull
    public BnfJoin getElementSyntax() {
        return elementSyntax;
    }

    @Nonnull
    public BnfJoin getSeparatorSyntax() {
        return separatorSyntax;
    }

    public void setElementSyntax(@Nonnull BnfJoin elementSyntax) {
        this.elementSyntax = elementSyntax;
    }

    public void setSeparatorSyntax(@Nonnull BnfJoin separatorSyntax) {
        this.separatorSyntax = separatorSyntax;
    }
}
