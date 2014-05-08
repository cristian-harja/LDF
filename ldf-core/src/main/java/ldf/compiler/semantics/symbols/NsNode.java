package ldf.compiler.semantics.symbols;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.Reference;
import ldf.compiler.context.ParserContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.*;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.*;

/**
 * <p>Represents a global tree-like namespace of symbols. Used by the
 * compiler to index packages, classes and grammars, and then resolve
 * import statements and other references.
 * </p>
 *
 * @author Cristian Harja
 */
@SuppressWarnings("unused")
@ThreadSafe
public final class NsNode {

    private final NsNodeType type;
    private final NsNode parent;
    private final String name;

    // children by name & type
    private Map<String, Multimap<NsNodeType, NsNode>> children;
    private Map<String, Multimap<NsNodeType, NsNode>> children2;
    private Map<String, Multimap<NsNodeType, NsNode>> readOnlyChildren;

    //TODO: group children by name ?
    //TODO: group children by type ?

    private List<NsNode> anonChildren;
    private List<NsNode> readOnlyAnonChildren = emptyList();

    private AstIdentifier astIdentifier;
    private AstNode astNode;

    protected NsNode(
            NsNodeType type,
            NsNode parent,
            String name
    ) {
        this.parent = parent;
        this.type = type;
        this.name = name;
    }

    public static NsNode initGlobalNS() {
        return new NsNode(NsNodeType.PACKAGE, null, null);
    }

    /**
     * Resolves a {@link ldf.compiler.ast.Reference}, provided a
     * {@link ldf.compiler.semantics.symbols.NsNode}.
     */
    public NsNode resolveSymbol(
            boolean testResolve,
            @Nonnull Reference ref,
            @Nonnull Iterator<AstIdentifier> it,
            @Nonnull NsNodeType[] pathTypes,
            @Nonnull NsNodeType[] targetTypes,
            boolean allowWildcard,
            boolean[] out_usedWildcard
    ) {
        assertValidNsNodeTypes(pathTypes, targetTypes);

        AstIdentifier id;
        String idName;
        boolean isLastId;
        NsNodeType[] types;
        Multimap<NsNodeType, NsNode> mm;
        NsNode current;

        current = this;

        do {
            id = it.next();
            idName = id.getName();
            isLastId = !it.hasNext();

            if (idName.equals("_")) {
                ParserContext ctx = id.getParserContext();
                if (!allowWildcard) {
                    if (!testResolve) {
                        ctx.reportError(id, ctx.i18n().getString(
                                "syntax.wildcard.not_allowed"
                        ));
                    }
                    return null;
                }
                if (!isLastId) {
                    if (!testResolve) {
                        ctx.reportError(id, ctx.i18n().getString(
                                "syntax.wildcard.misplaced"
                        ));
                    }
                    return null;
                }
                out_usedWildcard[0] = true;

                if (!testResolve) {
                    ref.setReferencedNsNode(current);
                }
                return current;
            }

            types = isLastId ? targetTypes : pathTypes;
            mm = current.getChildren().get(idName);

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
                // if not, report the error and quit
                if (!testResolve) {
                    reportSymbolsMismatch(
                            ref, id, targetTypes,
                            mm, matchingSymbols
                    );
                }
                return null;
            }

            current = getOnlyElement(mm.get(matchingType));

            if (isLastId) {
                if (!testResolve) {
                    ref.setReferencedNsNode(current);
                }
                return current;
            }

        } while (true);

    }

    static void reportSymbolsMismatch(
            Reference ref,
            AstIdentifier id,
            NsNodeType[] targetTypes,
            Multimap<NsNodeType, NsNode> foundSymbols,
            int matchingSymbols
    ) {
        String idName = id.getName();
        ParserContext ctx = ref.getParserContext();
        if (foundSymbols.isEmpty()) {
            ctx.reportError(id, ctx.i18n().getString(
                    "symbol.unresolved"
            ), idName, targetTypes, foundSymbols.keySet());
        } else if (matchingSymbols > 1) {
            ctx.reportError(id, ctx.i18n().getString(
                    "symbol.ambiguous"
            ), idName);
        } else {
            StringBuilder sb = new StringBuilder(32);

            formatTypeList(sb, Arrays.asList(targetTypes));
            String strTargetTypes = sb.toString();

            formatTypeList(sb, foundSymbols.keySet());
            String strFoundTypes = sb.toString();

            ctx.reportError(id, ctx.i18n().getString(
                    "reference.illegal.target"
            ), idName, strTargetTypes, strFoundTypes);
        }
    }

    private static void formatTypeList(
            StringBuilder sb, Collection<NsNodeType> types
    ) {
        boolean first = true;
        sb.setLength(0);
        if (types.size() != 1) sb.append('{');
        for(NsNodeType t : types) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(t);
        }
        if (types.size() != 1) sb.append('}');
    }

    static void assertValidNsNodeTypes(
            NsNodeType[] pathTypes,
            NsNodeType[] targetTypes
    ) {
        if (pathTypes.length == 0) {
            throw new IllegalArgumentException("Empty list `pathTypes`.");
        }
        if (targetTypes.length == 0) {
            throw new IllegalArgumentException("Empty list `targetTypes`.");
        }
    }

    public final NsNodeType getType() {
        return type;
    }

    @Nullable
    public final NsNode getParent() {
        return parent;
    }

    @Nullable
    public final String getName() {
        return name;
    }

    public boolean hasChild(
            @Nonnull String name,
            @Nonnull NsNodeType type
    ) {
        Multimap<NsNodeType, NsNode> symbols;
        symbols = children != null ? children.get(name) : null;
        return symbols != null && symbols.containsKey(name);
    }

    public synchronized Map<String, Multimap<NsNodeType, NsNode>>
    getChildren() {
        if (children == null) {
            return emptyMap();
        }
        return readOnlyChildren;
    }

    public synchronized Collection<NsNode> getChildren(
            @Nonnull NsNodeType type
    ) {
        if (children == null) {
            return emptyList();
        }
        Collection<NsNode> result = new ArrayList<NsNode>();
        for (Multimap<NsNodeType, NsNode> mm : children.values()) {
            result.addAll(mm.get(type));
        }
        return result;
    }

    public synchronized Collection<NsNode> getChildren(
            @Nonnull String name,
            @Nonnull NsNodeType type
    ) {
        if (children == null) return emptyList();
        Multimap<NsNodeType, NsNode> mm = children.get(name);
        if (mm == null) return emptyList();
        return mm.get(type);
    }

    /**
     *
     * @param id the name of the declared symbol
     * @param type the type of the declared symbol
     * @param astNode AST node corresponding to the entire declaration
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public synchronized NsNode declareChild(
            @Nonnull AstIdentifier id,
            @Nonnull NsNodeType type,
            @Nullable AstNode astNode
    ) {
        String name = id.getName();
        if (children == null) {
            children = new LinkedHashMap<String,
                    Multimap<NsNodeType, NsNode>>();
            children2 = new LinkedHashMap<String,
                    Multimap<NsNodeType, NsNode>>();
            readOnlyChildren = Collections.unmodifiableMap(children2);
        }
        Multimap<NsNodeType, NsNode> symbols = children.get(name);
        if (symbols == null) {
            symbols = ArrayListMultimap.create();
            children.put(name, symbols);
            children2.put(name, Multimaps.unmodifiableMultimap(symbols));
        }
        Collection<NsNode> nodes = symbols.get(type);
        NsNode result;
        if (!nodes.isEmpty() && !type.isSealed()) {
            result = getOnlyElement(nodes);
        } else {
            result = new NsNode(type, this, name);
            symbols.put(type, result);
        }

        // create references between these objects
        result.astIdentifier = id;
        id.setDeclaredNsNode(result);

        if (astNode != null) {
            result.astNode = astNode;
            astNode.setDeclaredNsNode(result);
        }

        return result;
    }

    @Nonnull
    public synchronized NsNode newAnonymousChild(
            @Nonnull NsNodeType type,
            @Nonnull AstNode astNode
    ) {
        String name = String.valueOf(readOnlyAnonChildren.size());
        NsNode result = new NsNode(type, this, name);
        if (anonChildren == null) {
            anonChildren = new ArrayList<NsNode>();
            readOnlyAnonChildren = unmodifiableList(anonChildren);
        }
        anonChildren.add(result);
        result.astNode = astNode;
        return result;
    }

    public List<NsNode> getAnonymousChildren() {
        return readOnlyAnonChildren;
    }

    @Nonnull
    public AstIdentifier getIdentifier() {
        return astIdentifier;
    }

    public AstNode getAstNode() {
        return astNode;
    }

    public Iterator<NsNode> findAllByDFS(
            @Nullable final Predicate<NsNode> P
    ) {
        Iterator<NsNode> it = new UnmodifiableIterator<NsNode>() {

            Stack<NsNode> q;

            {
                q = new Stack<NsNode>();
                q.add(NsNode.this);
            }

            @Override
            public boolean hasNext() {
                return !q.isEmpty();
            }

            @Override
            public NsNode next() {
                NsNode n = q.pop();
                if (n.children != null) {
                    for (Multimap<NsNodeType, NsNode> mm :
                            n.children.values()) {
                        q.addAll(mm.values());
                    }
                }
                return n;
            }
        };

        return P == null ? it : Iterators.filter(it, P);
    }

    public Iterator<NsNode> findAllByType(final NsNodeType t) {
        return findAllByDFS(new Predicate<NsNode>() {
            @Override
            public boolean apply(@Nullable NsNode input) {
                assert input != null;
                return input.type == t;
            }
        });
    }

    public void format(Appendable out) throws IOException {
        if (parent.name != null) {
            parent.format(out);
            out.append('.');
        }
        out.append(name);
    }
}
