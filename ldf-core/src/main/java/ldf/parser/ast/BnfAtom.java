package ldf.parser.ast;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class BnfAtom implements BnfObject {

    private String label;
    private BnfMultiplicity multiplicity;

    @Nonnull
    public final AtomType valueType;

    @Nonnull
    public final Object value;

    public BnfAtom(
            @Nonnull AtomType valueType,
            @Nonnull Object value
    ) {
        this.valueType = valueType;
        this.value = value;
    }

    public enum AtomType {
        REFERENCE,
        LITERAL_CHAR,
        LITERAL_STRING,
        BNF_JOIN,
        ALTERNATION
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BnfMultiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(BnfMultiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    @Override
    public Type getType() {
        return Type.ATOM;
    }
}
