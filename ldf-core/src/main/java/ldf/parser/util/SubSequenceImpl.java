package ldf.parser.util;

import javax.annotation.Nonnull;

/**
 * <p>References a portion of an existing {@link CharSequence} without
 * copying the character data.</p>
 *
 * <p>The {@link String#subSequence subSequence} method in {@link String}
 * and {@link StringBuilder} used to return similar objects, but because of
 * concerns about memory leaks, it now returns a brand new {@link String}.
 * </p>
 *
 * <p>This class is intended to be used by the LDF parser, in order to
 * retrieve portions of the input text. Memory leaks are NOT a concern
 * in this case because these objects are created after the parser already
 * decided to store the entire input into memory.</p>
 *
 * @see ldf.parser.LdfParser#getRecordedText
 *
 * @author Cristian Harja
 */
public final class SubSequenceImpl implements CharSequence {

    private final CharSequence orig;
    private final int offset, count;

    public SubSequenceImpl(CharSequence original, int start, int end) {
        assertValidOffsets(start, end, original.length());
        orig = original;
        offset = start;
        count = end - start;
    }

    public SubSequenceImpl(CharSequence original) {
        orig = original;
        offset = 0;
        count = original.length();
    }

    @Override
    public int length() {
        return count;
    }

    @Override
    public char charAt(int index) {
        return orig.charAt(offset + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        assertValidOffsets(start, end, count);
        return new SubSequenceImpl(orig, offset + start, end + offset);
    }

    @Nonnull
    @Override
    public String toString() {
        return orig.subSequence(offset, offset + count).toString();
    }

    private void assertValidOffsets(int start, int end, int length) {
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > length)
            throw new StringIndexOutOfBoundsException(end);
        if (start > end)
            throw new StringIndexOutOfBoundsException(end - start);
    }
}
