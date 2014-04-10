package ldf.parser.decl;

import ldf.java_cup.runtime.LocationAwareEntity;
import ldf.java_cup.runtime.LocationAwareEntityWrapper;
import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * @author Cristian Harja
 */
public class SymbolDef extends LocationAwareEntityWrapper {

    private Scope owner;
    private AstIdentifier name;
    private SymbolType type;
    private List<SymbolRef> backrefs;
    private List<SymbolRef> readOnlyBackref = emptyList();

    public SymbolDef(
            @Nonnull Scope owner,
            @Nonnull AstIdentifier name,
            @Nonnull SymbolType type) {
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    public Scope getOwner() {
        return owner;
    }

    public AstIdentifier getId() {
        return name;
    }

    public String getName() {
        return name.getName();
    }

    public SymbolType getType() {
        return type;
    }

    public List<SymbolRef> getBackReferences() {
        return readOnlyBackref;
    }

    public synchronized void addBackReference(SymbolRef symbolRef) {
        if (backrefs == null) {
            backrefs = new ArrayList<SymbolRef>();
            readOnlyBackref = unmodifiableList(backrefs);
        }
        backrefs.add(symbolRef);
    }

    @Override
    protected LocationAwareEntity getLocationAwareEntity() {
        return name;
    }
}
