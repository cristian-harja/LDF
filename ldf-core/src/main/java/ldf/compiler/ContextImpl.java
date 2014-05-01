package ldf.compiler;

import ldf.java_cup.runtime.LocationAwareEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;

/**
 * @author Cristian Harja
 */
public abstract class ContextImpl implements Context {
    private Locale locale;
    private CompilerLog logger;
    private ResourceBundle i18n;

    protected final void initLogger(Context ctx) {
        initLogger(
                ctx.getLocale(),
                ctx.i18n(),
                ctx.getLogger()
        );
    }

    protected final void initLogger(
            Locale locale,
            ResourceBundle i18n,
            CompilerLog logger
    ) {
        this.locale = locale;

        if (i18n == null) {
            if (locale != null) {
                i18n = getBundle("ldf.compiler.i18n", locale);
            } else {
                i18n = getBundle("ldf.compiler.i18n");
            }
        }
        this.i18n = i18n;

        if (logger == null) {
            logger = new CompilerLog();
        }
        this.logger = logger;

    }

    @Nonnull
    public final ResourceBundle i18n() {
        return i18n;
    }

    @Nonnull
    public final CompilerLog getLogger() {
        return logger;
    }

    @Nullable
    public final Locale getLocale() {
        return locale;
    }

    /**
     * Used to report an error to the compiler.
     */
    public void reportError(
            @Nullable LocationAwareEntity loc,
            @Nonnull String format,  Object... args
    ) {
        getLogger().logMessage(
                null, format,
                CompilerLog.EntryType.ERROR,
                loc, args
        );
    }

    /**
     * Used to report a warning to the compiler.
     */
    public void reportWarn(
            @Nullable LocationAwareEntity loc,
            @Nonnull String format, Object... args
    ) {
        getLogger().logMessage(
                null, format,
                CompilerLog.EntryType.WARN,
                loc, args
        );
    }
}

