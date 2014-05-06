package ldf.compiler.semantics.symbols;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public enum NsNodeType {
    PACKAGE,
    GRAMMAR,
    CLASS,
    VARIABLE,
    FUNCTION,
    NTERM;

    private int mask = 1 << ordinal();
    private int childrenMask;
    private int clashingMask;
    private boolean sealed = true;

    static {
        PACKAGE.sealed = false;
        NTERM.sealed = false;

        PACKAGE.childrenMask = PACKAGE.mask | GRAMMAR.mask;
        GRAMMAR.childrenMask = CLASS.mask | NTERM.mask;
        CLASS.childrenMask = VARIABLE.mask;
        NTERM.childrenMask = VARIABLE.mask;

        int clash =  PACKAGE.mask | CLASS.mask | GRAMMAR.mask;
        PACKAGE.clashingMask = clash;
        GRAMMAR.clashingMask = clash;
        CLASS.clashingMask = clash;
    }

    public boolean canContain(@Nonnull NsNodeType t) {
        return (childrenMask & t.mask) != 0;
    }

    public boolean clashesWith(@Nonnull NsNodeType t) {
        return (clashingMask & t.mask) != 0;
    }

    public boolean isSealed() {
        return sealed;
    }
}

