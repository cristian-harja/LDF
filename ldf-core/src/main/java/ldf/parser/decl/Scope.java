package ldf.parser.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static ldf.parser.Util.CollectionType.ARRAY_LIST;
import static ldf.parser.Util.NATIVE_HASH_COMPARATOR;
import static ldf.parser.Util.safeMapAdd;

/**
 * @author Cristian Harja
 */
@ThreadSafe
public class Scope implements Iterable<Scope> {

    private AstNode astNode;
    private Scope parentScope;
    private int bitMask;

    private final List<Scope>
            childScopes = new ArrayList<Scope>();

    private final Map<String, Collection<SymbolDef>>
            symbolDefs = new TreeMap<String, Collection<SymbolDef>>();

    private final Map<String, Collection<SymbolRef>>
            symbolRefs = new TreeMap<String, Collection<SymbolRef>>();

    private final List<Scope>
            readOnlyChildScopes = unmodifiableList(childScopes);

    private final Map<String, Collection<SymbolDef>>
            readOnlySymbolDefs = unmodifiableMap(symbolDefs);

    private final Map<String, Collection<SymbolRef>>
            readOnlySymbolRefs = unmodifiableMap(symbolRefs);

    private final Set<Scope>
            importedScopes = new TreeSet<Scope>(NATIVE_HASH_COMPARATOR);

    public Scope(Scope s, int acceptedTypes, AstNode astNode) {
        this.astNode = astNode;
        setParentScope(s);
        bitMask = acceptedTypes;
    }

    public void setParentScope(Scope parentScope) {
        Scope cursor = parentScope;
        while (cursor != null) {
            if (cursor == this) {
                throw new IllegalArgumentException(
                        "Cyclic parent-of relationship about to be created"
                );
            }
            cursor = cursor.getParentScope();
        }
        if (this.parentScope != null) {
            this.parentScope.childScopes.remove(this);
        }
        this.parentScope = parentScope;
        if (parentScope != null) {
            this.parentScope.childScopes.add(this);
        }
    }

    public Scope getParentScope() {
        return parentScope;
    }

    /**
     * A collection of symbols which were defined within this scope
     * (grouped by name).
     */
    @Nonnull
    public Map<String, Collection<SymbolDef>> getSymbolDefs() {
        return readOnlySymbolDefs;
    }

    /**
     * A collection of symbols which were referenced from within this
     * scope (grouped by name).
     */
    @Nonnull
    public Map<String, Collection<SymbolRef>> getSymbolRefs() {
        return readOnlySymbolRefs;
    }

    @Nonnull
    public List<Scope> getChildScopes() {
        return readOnlyChildScopes;
    }

    public SymbolDef defineSymbol(AstIdentifier name, SymbolType type) {
        if ((type.bitMask & bitMask) == 0) {
            Scope parent = getParentScope();
            if (parent == null) {
                throw new IllegalArgumentException(
                        "A symbol of this type " + type +
                        " is not accepted here"
                );
            }
            return parent.defineSymbol(name, type);
        }
        SymbolDef symInfo = new SymbolDef(this, name, type);
        safeMapAdd(symbolDefs, ARRAY_LIST, symInfo.getName(), symInfo);
        return symInfo;
    }

    public SymbolRef referenceSymbol(
            AstIdentifier name,
            int symbolTypesBitMask
    ) {
        SymbolRef symRef = new SymbolRef(name, symbolTypesBitMask);
        safeMapAdd(symbolRefs, ARRAY_LIST, symRef.getName(), symRef);
        return symRef;
    }

    protected void resolveReference(SymbolRef ref, boolean tryResolve,
                                    Set<Scope> visited) {
        String name = ref.getName();
        int typesMask = ref.getTypesMask();
        Scope s = this;
        if (visited.contains(this)) {
            return;
        }
        while (s != null) {
            if (!visited.contains(s) && (s.bitMask & typesMask) != 0) {
                visited.add(s);
                Collection<SymbolDef> syms = s.getSymbolDefs().get(name);
                if (syms != null) {
                    for (SymbolDef sym: syms) {
                        if ((sym.getType().bitMask & typesMask) == 0) {
                            continue;
                        }
                        ref.addCandidate(sym);
                    }
                }
                /*
                for (Scope is : importedScopes) {
                    is.resolveReference(ref, false, visited);
                    visited.add(is);
                }
                */
                if (tryResolve && ref.resolve() != null) {
                    return;
                }
            }

            s = s.parentScope;
        }
        for (Scope is : importedScopes) {
            is.resolveReference(ref, false, visited);
            visited.add(is);
        }
    }

    public void resolveReferences() {
        for (Map.Entry<String, Collection<SymbolRef>> entry :
                symbolRefs.entrySet()
        ) {
            for (SymbolRef ref : entry.getValue()) {
                resolveReference(ref, true,
                        new TreeSet<Scope>(NATIVE_HASH_COMPARATOR)
                );
            }
        }
        for (Scope child : childScopes) {
            child.resolveReferences();
        }
    }

    @Override
    public Iterator<Scope> iterator() {
        return getChildScopes().iterator();
    }

    public Iterator<Scope> iterateAll() {
        return new Iterator<Scope>() {

            private Stack<Scope> scopes = new Stack<Scope>();

            {
                scopes.add(Scope.this);
            }

            @Override
            public boolean hasNext() {
                return !scopes.isEmpty();
            }

            @Override
            public Scope next() {
                Scope top = scopes.pop();
                scopes.addAll(top.getChildScopes());
                return top;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int getBitMask() {
        return bitMask;
    }

    public AstNode getAstNode() {
        return astNode;
    }

    public void importScope(Scope scope) {
        importedScopes.add(scope);
    }

}
