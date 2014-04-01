package ldf.parser.ast.expr;

import ldf.parser.Util.ListBuilder;
import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.bnf.BnfAtom;
import ldf.parser.ast.bnf.BnfAtomType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * A "reference" is a fully qualified name (identifiers separated by dot).
 * Used in a couple of places. Subject to change.
 *
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class ExprReference extends Expression
        implements BnfAtom {
    @Nonnull
    private List<AstIdentifier> path;

    private ExprReference(
            @Nonnull List<AstIdentifier> path
    ) {
        this.path = path;
        addAstChildren(path);
    }

    @Nonnull
    public List<AstIdentifier> getPath() {
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
    public static class Builder
            extends ListBuilder<AstIdentifier, Builder> {

        public ExprReference build() {
            assertNotBuilt();
            return new ExprReference(buildList());
        }
    }
}
