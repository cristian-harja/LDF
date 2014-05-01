package ldf.compiler;

import ldf.compiler.semantics.types.TypeEnv;
import ldf.compiler.targets.java.types.JavaTypeEnv;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Cristian Harja
 */
public class LdfCompilerSettings {

    public static final Class<? extends TypeEnv>
            DEFAULT_TYPE_ENV = JavaTypeEnv.class;


    @Nonnull
    ArrayList<File> sources = new ArrayList<File>();

    @Nonnull
    Class<? extends TypeEnv> typeEnv = DEFAULT_TYPE_ENV;

    @Nullable
    CompilerLog logger;

    @Nullable
    Locale locale;

    @Nullable
    ResourceBundle i18n;

    public void addSourceFile(File f) {
        sources.add(f);
    }

    public void setTypeEnv(@Nonnull Class<? extends TypeEnv> typeEnv) {
        this.typeEnv = typeEnv;
    }

    public void setLogger(@Nullable CompilerLog logger) {
        this.logger = logger;
    }

    public void setLocale(@Nullable Locale locale) {
        this.locale = locale;
    }

    public void setI18n(@Nullable ResourceBundle i18n) {
        this.i18n = i18n;
    }

}
