package ldf.parser.ast.expr;

import ldf.parser.Util.ListBuilder;
import ldf.parser.ast.bnf.BnfAtom;
import ldf.parser.ast.bnf.BnfAtomType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * A "reference" is a fully qualified name (identifiers separated by dot).
 * Used in a couple of places. Subject to change.
 *
 *
 * @author Cristian Harja
 */
@Immutable
public final class ExprReference implements Expression, BnfAtom
{
    @Nonnull
    private List<String> path;

    private ExprReference(
            @Nonnull List<String> path
    ) {
        this.path = path;
    }

    @Nonnull
    public List<String> getPath() {
        return path;
    }

    /**
     * @return {@link BnfAtomType#REFERENCE}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.REFERENCE;
    }

    /**
     * Builds {@link ExprReference} objects.
     */
    @NotThreadSafe
    public static class Builder extends ListBuilder<String, Builder> {

        public ExprReference build() {
            assertNotBuilt();
            return new ExprReference(buildList());
        }
    }
}
