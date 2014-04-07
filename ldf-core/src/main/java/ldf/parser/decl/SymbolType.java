package ldf.parser.decl;

/**
 * @author Cristian Harja
 */
public enum SymbolType {

    GRAMMAR,
    NTERM,
    NTERM_LABEL,
    PLACEHOLDER,
    FUNCTION,
    FUNCTION_FORMAL_PARAM,
    VARIABLE;

    public final int bitMask = 1 << ordinal();

    public static final int bitMaskAll = (1 << values().length) - 1;

    public static int bitMask(SymbolType ... filter) {
        int filterBitMask = 0;
        for (SymbolType t: filter) {
            filterBitMask |= t.bitMask;
        }
        return filterBitMask;
    }
}
