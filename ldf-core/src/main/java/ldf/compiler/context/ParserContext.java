package ldf.compiler.context;

import ldf.java_cup.runtime.LocationAwareEntity;

import javax.annotation.Nonnull;
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

    void reportError(
            @Nullable LocationAwareEntity pos,
            @Nonnull String format, Object... args
    );

    void reportWarn(
            @Nullable LocationAwareEntity pos,
            @Nonnull String format, Object... args
    );


}
