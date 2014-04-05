package ldf.parser.util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;

/**
 * A {@link Reader} which saves a copy the read data into a {@link
 * StringBuilder} to be used later. This class also implements {@link
 * CharSequence}, as means of accessing the data.
 *
 * @author Cristian Harja
 */
public final class StreamRecorder extends Reader implements CharSequence{

    private final Reader original;
    private final StringBuilder sb;

    public StreamRecorder(Reader original) {
        this.original = original;
        sb = new StringBuilder();
    }

    public StreamRecorder(Reader original, int initialSize) {
        this.original = original;
        sb = new StringBuilder(initialSize);
    }

    @Override
    public int read(@Nonnull char[] cbuf, int off, int len)
            throws IOException {
        int read = original.read(cbuf, off, len);
        if (read != -1) {
            sb.append(cbuf, off, len);
        }
        return read;
    }

    @Override
    public void close() throws IOException {
        original.close();
    }

    @Override
    public int length() {
        return sb.length();
    }

    @Override
    public char charAt(int index) {
        return sb.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new SubSequenceImpl(sb, start, end);
    }
}
