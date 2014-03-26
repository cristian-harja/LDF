package ldf.parser.ast.expr;

import ldf.parser.ast.bnf.BnfAtom;
import ldf.parser.ast.bnf.BnfAtomType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * String literal. Defined as a list of fragments, in order to be able to
 * handle invalid bits of text separately (illegal characters, invalid
 * escape sequence).
 *
 * @author Cristian Harja
 */
@Immutable
public final class LiteralString implements ExprLiteral, BnfAtom
{
    @Nonnull
    private List<Fragment> frags;

    @Nonnull
    private List<Fragment> invalid;

    private boolean isCharLiteral;

    public boolean hasInvalidFragments() {
        return invalid.size() != 0;
    }

    private LiteralString() {}

    @Nonnull
    public List<Fragment> getFragments() {
        return frags;
    }

    @Nonnull
    public List<Fragment> getInvalidFragments() {
        return invalid;
    }

    public boolean isCharLiteral() {
        return isCharLiteral;
    }

    /**
     * @return {@link BnfAtomType#LITERAL_CHAR} (when
     *         {@link #isCharLiteral isCharLiteral()} returns true) or
     *         {@link BnfAtomType#LITERAL_STRING} (otherwise).
     */
    @Override
    public final BnfAtomType getBnfAtomType() {
        return isCharLiteral
            ?   BnfAtomType.LITERAL_CHAR
            :   BnfAtomType.LITERAL_STRING;
    }

    @Immutable
    private static class Fragment {
        public String fragment;
        public boolean isValid = true;
    }

    /**
     * Builds {@link LiteralString} objects.
     */
    @NotThreadSafe
    public static class Builder {
        private List<Fragment> frags = new ArrayList<Fragment>();
        private List<Fragment> invalid = new ArrayList<Fragment>();

        public Builder add(String f, boolean valid) {
            Fragment frag = new Fragment();
            frag.fragment = f;
            frag.isValid = valid;
            frags.add(frag);
            if (!valid) {
                invalid.add(frag);
            }
            return this;
        }

        public LiteralString build(boolean isCharLiteral) {
            LiteralString literal = new LiteralString();
            literal.isCharLiteral = isCharLiteral;
            literal.frags = unmodifiableList(frags);
            literal.invalid = invalid.size() != 0
                    ? unmodifiableList(invalid)
                    : Collections.<Fragment>emptyList();
            return literal;
        }
    }
}
