package ldf.compiler.semantics.symbols;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.Reference;
import ldf.compiler.context.ParserContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static ldf.compiler.semantics.symbols.NsNodeType.*;

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
@ThreadSafe
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

    /**
     * Resolves a {@link Reference}, provided a {@link Scope}.
     */
    @Nullable
    public NsNode resolveSymbol(
            boolean testResolve,
            @Nonnull Reference ref,
            @Nonnull NsNodeType[] pathTypes,
            @Nonnull NsNodeType[] targetTypes,
            boolean allowWildcard,
            boolean[] out_usedWildcard
    ) {
        NsNode.assertValidNsNodeTypes(pathTypes, targetTypes);

        Iterator<AstIdentifier> it;
        AstIdentifier id;
        String idName;
        boolean isLastId;
        NsNodeType[] types;
        Multimap<NsNodeType, NsNode> mm;

        it = ref.getPath().iterator();
        id = it.next();
        idName = id.getName();

        Scope s = this;

        if (idName.equals("_")) {
            // jump to the global scope
            while (s.parentTbl != null) {
                s = s.parentTbl;
            }

            id = it.next();
            idName = id.getName();
            isLastId = !it.hasNext();
            types = isLastId ? targetTypes : pathTypes;

        } else {
            isLastId = !it.hasNext();
            types = isLastId ? targetTypes : pathTypes;
        }

        do {

            // look for symbols with the correct name in this scope
            mm = s.currentNs.get(idName);
            s = s.parentTbl;

            // if none found, continue with the parent scope
            if (mm == null) continue;

            // if some symbols with the correct name were found,
            // see if any are of the correct type
            int matchingSymbols = 0;
            NsNodeType matchingType = null;
            for (NsNodeType t : types) {
                if (mm.containsKey(t)) {
                    ++matchingSymbols;
                    matchingType = t;
                }
            }

            // there should be exactly one symbol with the correct type
            if (matchingSymbols != 1) {
                // if there wasn't one, we should keep looking in the
                // parent scopes
                if (mm.isEmpty()) {
                    continue;
                }

                if (!testResolve) {
                    // if there are more than one possible symbols,
                    // report the error and quit
                    NsNode.reportSymbolsMismatch(
                            ref, id, targetTypes, mm, matchingSymbols
                    );
                }
                return null;
            }

            // there was exactly one symbol with a matching type
            NsNode result = getOnlyElement(mm.get(matchingType));
            if (isLastId) {
                if (!testResolve) {
                    ref.setReferencedNsNode(result);
                }
                return result;
            } else {
                return result.resolveSymbol(
                        testResolve,
                        ref, it,
                        pathTypes, targetTypes,
                        allowWildcard,
                        out_usedWildcard
                );
            }

        } while (s != null);

        if (!testResolve) {
            ParserContext ctx = ref.getParserContext();
            ctx.reportError(id, ctx.i18n().getString(
                    "symbol.unresolved"
            ), id);
        }

        return null;
    }

    private static final NsNodeType[]
            STATICS = new NsNodeType[]{GRAMMAR, PACKAGE, CLASS},
            GRAMMAR_ = new NsNodeType[]{GRAMMAR},
            CLASS_ = new NsNodeType[]{CLASS},
            NTERM_ = new NsNodeType[]{NTERM};


    @Nullable
    public NsNode resolveStatic(
            boolean testResolve,
            @Nonnull Reference ref,
            boolean allowWildcard,
            boolean[] out_usedWildcard
    ) {
        return resolveSymbol(
                testResolve,
                ref, STATICS, STATICS,
                allowWildcard, out_usedWildcard
        );
    }

    public NsNode resolveGrammar(
            @Nonnull Reference ref
    ) {
        return resolveSymbol(false, ref, STATICS, GRAMMAR_, false, null);
    }

    public NsNode resolveClass(
            @Nonnull Reference ref
    ) {
        return resolveSymbol(false, ref, STATICS, CLASS_, false, null);
    }

    public NsNode tryResolveClass(
            @Nonnull Reference ref
    ) {
        return resolveSymbol(true, ref, STATICS, CLASS_, false, null);
    }

    public NsNode resolveNterm(
            @Nonnull Reference ref
    ) {
        return resolveSymbol(false, ref, STATICS, NTERM_, false, null);
    }

    public NsNode resolveStatic(
            Reference importRef,
            boolean allowWildcard,
            boolean[] out_usedWildcard
    ) {
        return resolveStatic(
                true, importRef, allowWildcard, out_usedWildcard
        );
    }
}


