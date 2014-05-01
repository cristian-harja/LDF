package ldf.compiler;

import javax.annotation.Nullable;

/**
 * An interface for interacting with the parser.
 *
 * @author Cristian Harja
 */
public interface ParserContext extends Context {

    /**
     * @return name of the file currently being parser (null if not known)
     */
    @Nullable
    String getFileName();

}
