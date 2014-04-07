package ldf.parser.decl;

import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableMap;
import static ldf.parser.Util.CollectionType.ARRAY_LIST;
import static ldf.parser.Util.safeMapAdd;

/**
 * @author Cristian Harja
 */
@ThreadSafe
public class Scope {

    private Scope parentScope;

    private final Map<String, Collection<SymbolDef>>
            symbolDefs = new TreeMap<String, Collection<SymbolDef>>();

    private final Map<String, Collection<SymbolRef>>
            symbolRefs = new TreeMap<String, Collection<SymbolRef>>();

    private final Map<String, Collection<SymbolDef>>
            readOnlySymbolDefs = unmodifiableMap(symbolDefs);

    private final Map<String, Collection<SymbolRef>>
            readOnlySymbolRefs = unmodifiableMap(symbolRefs);

    public Scope() {}

    public Scope(Scope s) {
        setParentScope(s);
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
        this.parentScope = parentScope;
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

    public SymbolDef defineSymbol(AstIdentifier name, SymbolType type) {
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


}
