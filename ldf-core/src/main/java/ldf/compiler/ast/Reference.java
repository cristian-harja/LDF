package ldf.compiler.ast;

import ldf.compiler.ast.bnf.BnfAtom;
import ldf.compiler.ast.bnf.BnfAtomType;
import ldf.compiler.ast.decl.DeclGrammar;
import ldf.compiler.ast.expr.Expression;
import ldf.compiler.semantics.ags.AgsNode;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;
import ldf.compiler.util.Util;
import ldf.compiler.util.Util.ListBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    private AgsNode agsNode;

    boolean resolveAttempted;

    private Reference(
            @Nonnull List<AstIdentifier> path
    ) {
        this.path = path;
        addAstChildren(path);
        agsNode = AgsNode.agsInit(this);
    }

    @Nonnull
    public List<AstIdentifier> getPath() {
        return path;
    }

    // helper
    @Nullable
    public NsNode resolve(
            NsNodeType[] pathTypes,
            NsNodeType[] targetTypes,
            boolean allowWildcard,
            boolean[] out_usedWildcard
    ) {
        Util.assertSetOnce(resolveAttempted, "resolve*");
        NsNode node = getScope().resolveSymbol(
                false, this, pathTypes, targetTypes,
                allowWildcard, out_usedWildcard
        );
        resolveAttempted = true;
        return node;
    }

    @Override
    public void setReferencedNsNode(NsNode referencedNsNode) {
        Util.assertSetOnce(resolveAttempted, "resolve");
        resolveAttempted = true;
        super.setReferencedNsNode(referencedNsNode);
    }

    @Nullable
    public NsNode resolveAsClass() {
        Util.assertSetOnce(resolveAttempted, "resolve*");
        return getScope().resolveClass(this);
    }

    @Nullable
    public NsNode tryResolveAsClass() {
        Util.assertSetOnce(resolveAttempted, "resolve*");
        return getScope().tryResolveClass(this);
    }

    @Nullable
    public NsNode resolveAsGrammar() {
        Util.assertSetOnce(resolveAttempted, "resolve*");
        return getScope().resolveGrammar(this);
    }

    public NsNode resolveAsNterm(DeclGrammar parentGrammar) {
        Util.assertSetOnce(resolveAttempted, "resolve*");
        return parentGrammar.getScope().resolveNterm(this);
    }

    public boolean isResolveAttempted() {
        return resolveAttempted;
    }

    public boolean isResolved() {
        return getReferencedNsNode() != null;
    }


    /**
     * @return {@link BnfAtomType#REFERENCE}.
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return BnfAtomType.REFERENCE;
    }

    @Nonnull
    @Override
    public AgsNode getAbstractGrammarSpec() {
        return agsNode;
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
