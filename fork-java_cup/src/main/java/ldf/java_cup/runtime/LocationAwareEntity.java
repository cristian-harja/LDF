package ldf.java_cup.runtime;

/**
 * Base class for {@link Symbol} and {@link Comment}, both of which
 * need position information (line number, column number and absolute
 * offset) about themselves.
 *
 * @author Cristian Harja
 */
@SuppressWarnings("unused")
public class LocationAwareEntity {

    protected int lineL, columnL, offsetL = -1;
    protected int lineR, columnR, offsetR = -1;

    public void setLeftPos(int line, int column, int offset) {
        lineL = line;
        columnL = column;
        offsetL = offset;
    }

    public void setRightPos(int line, int column, int offset) {
        lineR = line;
        columnR = column;
        offsetR = offset;
    }

    public int getLineL() {
        return lineL;
    }

    public int getLineR() {
        return lineR;
    }

    public int getColumnL() {
        return columnL;
    }

    public int getColumnR() {
        return columnR;
    }

    public int getOffsetL() {
        return offsetL;
    }

    public int getOffsetR() {
        return offsetR;
    }

}
