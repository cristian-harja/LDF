package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static ldf.parser.ast.BnfMultiplicity.Validation.*;

/**
 * @author Cristian Harja
 */
@Immutable
public final class BnfMultiplicity {

    @Nonnull  private String pattern;
    @Nullable private Number min;
    @Nullable private Number max;
    @Nonnull  private Validation validation;

    public BnfMultiplicity(
            @Nonnull String pattern,
            @Nullable Number min,
            @Nullable Number max
    ) {
        this.pattern = pattern;
        this.min = min;
        this.max = max;
        validation = validateRange(min, max);
    }

    @Nonnull
    public String getPattern() {
        return pattern;
    }

    @Nullable
    public Number getMin() {
        return min;
    }

    @Nullable
    public Number getMax() {
        return max;
    }

    @Nonnull
    public Validation getValidation() {
        return validation;
    }

    public enum Validation {
        VALID,
        MIN_NOT_INTEGER,
        MAX_NOT_INTEGER,
        MIN_IS_NEGATIVE,
        MAX_IS_NEGATIVE,
        MAX_LESS_THAN_MIN
    }

    /**
     * <p>Ensures that {@code min} and {@code max} are both valid by
     * themselves (either {@code null} or positive) and if both are
     * non-null, checks that {@code min <= max}.
     * </p>
     * <p>If some of these conditions are not met, the returned value
     * describes the error.</p>
     */
    private static Validation validateRange(Number min, Number max) {
        long n, m;

        if (min != null) {
            if (min instanceof Double || min instanceof Float)
                return MIN_NOT_INTEGER;

            n = min.longValue();

            if (n < 0) return MIN_IS_NEGATIVE;

        } else {
            n = 0;
        }

        if (max != null) {
            if (max instanceof Double || max instanceof Float)
                return MAX_NOT_INTEGER;

            m = max.longValue();

            if (m < 0) return MAX_IS_NEGATIVE;
            if (m < n) return MAX_LESS_THAN_MIN;
        }

        return VALID;
    }

}
