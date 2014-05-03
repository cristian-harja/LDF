package ldf.compiler.semantics.symbols;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
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
        id.setReferencedNsNode(result);
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

}
