package ldf.parser.ast;

/**
 * @author Cristian Harja
 */
public interface BnfObject {

    enum Type {
        JOIN, MEET, ATOM, ACTION, GUARD, PLACEHOLDER
    }

    Type getType();


}
