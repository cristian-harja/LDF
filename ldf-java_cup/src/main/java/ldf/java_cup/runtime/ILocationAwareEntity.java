package ldf.java_cup.runtime;

/**
 * Represents an entity which has access to position information about
 * itself. The object could be storing this information itself (ex: a
 * sub-class of {@link LocationAwareEntityImpl})or be a wrapper over
 * another object which stores this info (ex: a sub-class of {@link
 * ldf.java_cup.runtime.LocationAwareEntityWrapper}).
 *
 * @see LocationAwareEntityImpl
 * @see LocationAwareEntityWrapper
 *
 * @author Cristian Harja
 */
public interface ILocationAwareEntity {

    void setLeftPos(ILocationAwareEntity left);
    void setLeftPos(int line, int column, int offset);

    void setRightPos(ILocationAwareEntity right);
    void setRightPos(int line, int column, int offset);

    int getLineL();
    int getLineR();

    int getColumnL();
    int getColumnR();

    int getOffsetL();
    int getOffsetR();

}
