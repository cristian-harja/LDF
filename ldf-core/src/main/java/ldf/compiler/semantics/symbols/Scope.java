package ldf.compiler.semantics.symbols;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.Reference;
import ldf.compiler.context.ParserContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Iterables.getOnlyElement;
import static ldf.compiler.semantics.symbols.NsNodeType.CLASS;
import static ldf.compiler.semantics.symbols.NsNodeType.GRAMMAR;
import static ldf.compiler.semantics.symbols.NsNodeType.PACKAGE;

/**
 * <p>A catalog / index of symbols which are visible at a certain point in
 * the source code. Internally, symbols are first grouped by name, then by
 * type.
 * </p>
 * <p>Unlike {@code NsNode} (which groups symbols semantically, by their
 * location in a package, class or grammar), this class indexes symbols
 * by their visibility according to <em>syntactic</em> rules.
 * </p>
 *
 * @author Cristian Harja
 */
public final class Scope {

    private Map<String, Multimap<NsNodeType, NsNode>> currentNs;

    private Scope parentTbl;

    public Scope() {
        this(null);
    }

    private Scope(Scope parent) {
        parentTbl = parent;
        currentNs = new LinkedHashMap<String, Multimap<NsNodeType, NsNode>>();
    }

    public Scope fork() {
        return new Scope(this);
    }

    public void importAll(NsNode nameSpace) {
        Map<String, Multimap<NsNodeType, NsNode>> children;
        children = nameSpace.getChildren();
        for (Map.Entry<String, Multimap<NsNodeType, NsNode>> e :
                children.entrySet()) {
            String name = e.getKey();
            Multimap<NsNodeType, NsNode> mm = children.get(name);
            if (mm != null) {
                currentNs.put(name, mm);
            }
        }
    }

    public void importOne(
            @Nonnull NsNode nsNode,
            @Nullable String asName
    ) {
        if (asName == null) {
            asName = nsNode.getName();
            if (asName == null) {
                throw new RuntimeException(
                        "Trying to import an anonymous symbol by its name."
                );
            }
        }
        Multimap<NsNodeType, NsNode> mm = currentNs.get(asName);
        if (mm == null) {
            mm = ArrayListMultimap.create();
            currentNs.put(asName, mm);
        }
        mm.put(nsNode.getType(), nsNode);
    }

    @Nonnull
    public Multimap<NsNodeType, NsNode> resolveSymbol(
            @Nonnull String name, NsNodeType... includedTypes
    ) {
        if (includedTypes.length == 0) {
            return ImmutableMultimap.of();
        }
        Scope tbl = this;
        do {
            Multimap<NsNodeType, NsNode> mm = tbl.currentNs.get(name);
            tbl = tbl.parentTbl;
            if (mm == null) continue;
            for (NsNodeType t : mm.keySet()) {
                if (mm.containsKey(t)) {
                    return mm;
                }
            }
        } while (tbl != null);
        return ImmutableMultimap.of();
    }

    @Nullable
    public NsNode resolveStatic(
            @Nonnull Reference ref,
            boolean allowWildcard,
            boolean[] out_usedWildcard
    ) {
        NsNode node = null;
        int i = 0, n = ref.getPath().size();

        for (AstIdentifier id : ref.getPath()) {
            Multimap<NsNodeType, NsNode> mm;
            String name = id.getName();
            if (node == null) {
                mm = resolveSymbol(name, GRAMMAR, PACKAGE, CLASS);
            } else {
                mm = node.getChildren().get(name);
            }

            if (name.equals("_")) {
                ParserContext ctx = id.getParserContext();
                if (i != n - 1) {
                    ctx.reportError(id, ctx.i18n().getString(
                            "syntax.wildcard.misplaced"
                    ), id.getName());
                } else if (!allowWildcard) {
                    ctx.reportError(id, ctx.i18n().getString(
                            "syntax.wildcard.not_allowed"
                    ), id.getName());
                } else {
                    out_usedWildcard[0] = true;
                }
                break;
            }

            if (mm == null) {
                mm = ImmutableListMultimap.of(); // empty multi-map
            }

            Collection<NsNode> classes = mm.get(CLASS);
            Collection<NsNode> packages = mm.get(PACKAGE);
            Collection<NsNode> grammars = mm.get(GRAMMAR);
            int total = classes.size() + packages.size() + grammars.size();

            if (total != 1) {
                ParserContext ctx = id.getParserContext();
                ctx.reportError(id, ctx.i18n().getString(
                        total == 0
                                ? "symbol.unresolved"
                                : "symbol.ambiguous"
                ), id.getName());
                return null;
            }

            if (!classes.isEmpty()) {
                node = getOnlyElement(classes);
            } else if (!packages.isEmpty()) {
                node = getOnlyElement(packages);
            } else {
                node = getOnlyElement(grammars);
            }

            id.setReferencedNsNode(node);
            ++i;
        }
        ref.setReferencedNsNode(node);
        return node;
    }

}


