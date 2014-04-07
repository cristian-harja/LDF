package ldf.parser.decl;

import ldf.parser.ast.AstIdentifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Cristian Harja
 */
public class SymbolDef {

    private Scope owner;
    private AstIdentifier name;
    private SymbolType type;
    private List<SymbolRef> backrefs;

    public SymbolDef(
            @Nonnull Scope owner,
            @Nonnull AstIdentifier name,
            @Nonnull SymbolType type) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        backrefs = new ArrayList<SymbolRef>();
    }

    public Scope getOwner() {
        return owner;
    }

    public String getName() {
        return name.getName();
    }

    public SymbolType getType() {
        return type;
    }

    public List<SymbolRef> getBackrefs() {
        return Collections.unmodifiableList(backrefs);
    }
}
