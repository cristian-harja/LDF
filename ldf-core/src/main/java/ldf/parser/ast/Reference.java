package ldf.parser.ast;

import ldf.parser.Util.ListBuilder;
import ldf.parser.ast.bnf.BnfAtom;
import ldf.parser.ast.bnf.BnfAtomType;
import ldf.parser.ast.expr.Expression;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * A "reference" is a fully qualified name (identifiers separated by dot).
 * Used in a couple of places. Subject to change.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class Reference extends Expression
        implements BnfAtom {
    @Nonnull
    private List<AstIdentifier> path;

    private Reference(
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
     * Builds {@link Reference} objects.
     */
    @NotThreadSafe
    public static class Builder
            extends ListBuilder<AstIdentifier, Builder> {

        public Reference build() {
            assertNotBuilt();
            return new Reference(buildList());
        }
    }
}
