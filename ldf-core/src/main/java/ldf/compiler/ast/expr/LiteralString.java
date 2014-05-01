package ldf.compiler.ast.expr;

import ldf.compiler.ags.AgsNode;
import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.bnf.BnfAtom;
import ldf.compiler.ast.bnf.BnfAtomType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
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
@ThreadSafe
public final class LiteralString extends ExprLiteral
        implements BnfAtom {
    @Nonnull
    private List<Fragment> frags;

    @Nonnull
    private List<Fragment> invalid;

    private boolean isCharLiteral;

    private AgsNode agsNode;

    public boolean hasInvalidFragments() {
        return invalid.size() != 0;
    }

    private LiteralString() {
        agsNode = AgsNode.agsInit(this);
    }

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

    @Nonnull
    @Override
    public AgsNode getAbstractGrammarSpec() {
        return agsNode;
    }

    /**
     * A portion of a string which can be flagged as invalid,
     * or hightlighted differently in an IDE (escape sequence).
     */
    @Immutable
    public static class Fragment extends AstNode{
        private String fragment;
        private boolean isValid;

        public Fragment(String fragment, boolean isValid) {
            this.fragment = fragment;
            this.isValid = isValid;
        }

        public String getString() {
            return fragment;
        }

        public boolean isValid() {
            return isValid;
        }
    }

    /**
     * Builds {@link LiteralString} objects.
     */
    @NotThreadSafe
    public static class Builder {
        private List<Fragment> frags = new ArrayList<Fragment>();
        private List<Fragment> invalid = new ArrayList<Fragment>();

        public Builder add(Fragment frag) {
            frags.add(frag);
            if (!frag.isValid()) {
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
            literal.addAstChildren(frags);
            return literal;
        }
    }
}
