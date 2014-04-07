package ldf.parser.decl;

import ldf.parser.ast.AstIdentifier;

/**
 * @author Cristian Harja
 */
public class SymbolRef {
    private AstIdentifier name;
    private int typesMask;

    protected SymbolDef resolvedTo;

    public SymbolRef(AstIdentifier name, SymbolType... symbolTypes) {
        this(name, SymbolType.bitMask(symbolTypes));
    }

    public SymbolRef(AstIdentifier name, int bitMask) {
        this.name = name;
        this.typesMask = bitMask;
    }

    public String getName() {
        return name.getName();
    }

    public int getTypesMask() {
        return typesMask;
    }
}
