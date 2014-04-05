package ldf.parser.inspect;

import ldf.java_cup.runtime.LocationAwareEntity;

import java.io.IOException;
import java.util.Comparator;

/**
 * Basic class for passing around the results of an inspection.
 * I don't think getters and setters are worth the trouble here.
 *
 * @author Cristian Harja
 */
public class Result {

    /**
     * An instance of {@link ldf.java_cup.runtime.LocationAwareEntity} (or
     * a sub-class) whose location can help the user find the problem.
     *
     * This can be, for example a {@link ldf.java_cup.runtime.Symbol} or
     * a {@link ldf.parser.st.StNode} object (from the syntax tree).
     */
    public LocationAwareEntity pos;

    public Type type;
    public String fileName;

    /**
     * Message (error, warning, etc.) to be displayed to the user.
     */
    public String msg;

    public static enum Type {
        ERROR, WARN // more can be added
    }

    public void format(Appendable out) throws IOException{
        out.append('[');
        out.append(String.valueOf(type));
        out.append("] ");

        if (fileName != null) {
            out.append(fileName);
        } else {
            out.append("<unknown>");
        }
        out.append(":");

        out.append(String.valueOf(pos.getLineL()));
        out.append(":");
        out.append(String.valueOf(pos.getColumnL()));
        out.append("-");
        out.append(String.valueOf(pos.getColumnR()));
        out.append(": ");

        out.append(msg);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        try {
            format(sb);
        } catch (IOException ignored) {
        }
        return sb.toString();
    }

    public static final Comparator<Result> COMPARATOR
            = new Comparator<Result>() {
        @Override
        public int compare(Result o1, Result o2) {
            int x1, x2;
            x1 = o1.pos.getOffsetL();
            x2 = o2.pos.getOffsetL();
            if (x1 != x2) return x1 - x2;

            x1 = o1.pos.getOffsetR();
            x2 = o2.pos.getOffsetR();
            if (x1 != x2) return x1 - x2;

            x1 = o1.hashCode();
            x2 = o2.hashCode();

            return x1 - x2;
        }
    };
}