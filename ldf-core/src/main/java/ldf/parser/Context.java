package ldf.parser;

import ldf.parser.inspect.Result;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An interface for interacting with the parser.
 *
 * @author Cristian Harja
 */
public interface Context {

    /**
     * @return name of the file currently being parser (null if not known)
     */
    @Nullable
    String getFilename();

    /**
     * Used to report an error / warning to the parser.
     */
    void report(@Nonnull Result result);
}
