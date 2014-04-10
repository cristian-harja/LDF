package ldf.parser.decl;

import ldf.java_cup.runtime.LocationAwareEntity;
import ldf.java_cup.runtime.LocationAwareEntityWrapper;
import ldf.parser.Util;
import ldf.parser.ast.AstIdentifier;

import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

/**
 * @author Cristian Harja
 */
public class SymbolRef extends LocationAwareEntityWrapper {
    private AstIdentifier name;
    private int typesMask;

    private Set<SymbolDef> candidates;

    private Set<SymbolDef> readOnlyCandidates = emptySet();

    protected SymbolDef resolvedTo;

    public SymbolRef(AstIdentifier name, SymbolType... symbolTypes) {
        this(name, SymbolType.bitMask(symbolTypes));
    }

    public SymbolRef(AstIdentifier name, int bitMask) {
        this.name = name;
        this.typesMask = bitMask;
    }

    public AstIdentifier getId() {
        return name;
    }

    public String getName() {
        return name.getName();
    }

    public int getTypesMask() {
        return typesMask;
    }

    public Set<SymbolDef> getCandidates() {
        return readOnlyCandidates;
    }

    public synchronized void addCandidate(SymbolDef sym) {
        if (candidates == null) {
            candidates = new TreeSet<SymbolDef>(
                    Util.NATIVE_HASH_COMPARATOR
            );
            readOnlyCandidates = unmodifiableSet(candidates);
        }
        candidates.add(sym);
    }

    public synchronized SymbolDef resolve() {
        if (resolvedTo != null) {
            return resolvedTo;
        }
        if (candidates != null && candidates.size() == 1) {
            resolvedTo = candidates.iterator().next();
            resolvedTo.addBackReference(this);
            return resolvedTo;
        }
        return null;
    }

    @Override
    protected LocationAwareEntity getLocationAwareEntity() {
        return getId();
    }
}
