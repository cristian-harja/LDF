package ldf.parser.inspect;

/**
 * @author Cristian Harja
 */
public interface Receiver<T> {
    void recv(T object, Object data);
}
