package ldf.compiler.context;

import ldf.compiler.CompilerLog;
import ldf.java_cup.runtime.LocationAwareEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Cristian Harja
 */
public interface Context {

    @Nullable
    Locale getLocale();

    @Nonnull
    ResourceBundle i18n();

    @Nonnull
    CompilerLog getLogger();

    /**
     * Used to report an error to the compiler.
     */
    void reportError(
            @Nullable String fileName,
            @Nullable LocationAwareEntity pos,
            @Nonnull String format, Object... args
    );

    /**
     * Used to report a warning to the compiler.
     */
    void reportWarn(
            @Nullable String fileName,
            @Nullable LocationAwareEntity pos,
            @Nonnull String format, Object... args
    );

}
