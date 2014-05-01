package ldf.compiler.semantics.symbols;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

import static java.util.Collections.*;

/**
 * @author Cristian Harja
 */
@SuppressWarnings("unused")
@ThreadSafe
public abstract class NsNode {

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

    private List<AstIdentifier> astBackRefIds;
    private List<AstIdentifier> readOnlyAstBackRefIds;

    protected NsNode(
            NsNodeType type,
            NsNode parent,
            String name
    ) {
        this.parent = parent;
        this.type = type;
        this.name = name;
        astBackRefIds = new LinkedList<AstIdentifier>();
        readOnlyAstBackRefIds = unmodifiableList(astBackRefIds);
    }

    public static NsNode initGlobalNS() {
        return NsNodeFactory.create(NsNodeType.PACKAGE, null, null, null);
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

    @SuppressWarnings("unchecked")
    @Nonnull
    public synchronized NsNode newChild(
            @Nonnull AstIdentifier id,
            @Nonnull NsNodeType type,
            @Nonnull AstNode astNode
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
            result = nodes.iterator().next();
        } else {
            result = NsNodeFactory.create(type, this, name, astNode);
            symbols.put(type, result);
        }
        result.astBackRefIds.add(id);
        return result;
    }

    @Nonnull
    public synchronized NsNode newAnonymousChild(
            @Nonnull NsNodeType type,
            @Nonnull AstNode astNode
    ) {
        NsNode result = NsNodeFactory.create(type, this, name, astNode);
        if (anonChildren == null) {
            anonChildren = new ArrayList<NsNode>();
            readOnlyAnonChildren = unmodifiableList(anonChildren);
        }
        anonChildren.add(result);
        return result;
    }

    public List<NsNode> getAnonymousChildren() {
        return readOnlyAnonChildren;
    }

    @Nonnull
    public List<AstIdentifier> getIdentifiers() {
        return readOnlyAstBackRefIds;
    }

}
