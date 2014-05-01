package ldf.compiler.ast.bnf;

import ldf.compiler.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Holds information about how a BNF item may be repeated in the input.
 * The syntax is similar to that of quantifiers from regular expressions:
 * <ul>
 * <li>{@code ?}     - 0 or 1</li>
 * <li>{@code *}     - 0 or more</li>
 * <li>{@code +}     - 1 or more</li>
 * <li>{@code {n}}   - exactly n</li>
 * <li>{@code {n,}}  - n or more</li>
 * <li>{@code {,m}}  - m or less</li>
 * <li>{@code {n,m}} - no less than n, no more than m</li>
 * </ul>
 *
 * Please note that there is no support for greedy/reluctant quantifiers
 * (like there is in some regular expressions).
 *
 * @author Cristian Harja
 */
@Immutable
public final class BnfQuantifier extends AstNode {

    @Nonnull  private String pattern;
    @Nullable private Number min;
    @Nullable private Number max;

    /**
     * @param pattern the pattern used to create this quantifier
     * @param min the minimum value (or null if unbounded)
     * @param max the maximum value (or null if unbounded)
     */
    public BnfQuantifier(
            @Nonnull String pattern,
            @Nullable Number min,
            @Nullable Number max
    ) {
        this.pattern = pattern;
        this.min = min;
        this.max = max;
    }

    /**
     * @return the pattern used to create this quantifier
     */
    @Nonnull
    public String getPattern() {
        return pattern;
    }

    /**
     * @return the minimum value (or {@literal null} if unbounded)
     */
    @Nullable
    public Number getMin() {
        return min;
    }

    /**
     * @return the maximum value (or {@literal null} if unbounded)
     */
    @Nullable
    public Number getMax() {
        return max;
    }

}
