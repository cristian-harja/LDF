package ldf.parser.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;

/**
 * @author Cristian Harja
 */
@Immutable
public final class DeclGrammar extends Declaration {

    @Nonnull
    private String name;

    @Nullable
    private List<ExprReference> extended;

    @Nonnull
    private DeclList declarations;

    public DeclGrammar(
            @Nonnull String name,
            @Nullable List<ExprReference> extended,
            @Nonnull DeclList declarations
    ) {
        this.name = name;
        this.extended = extended == null ? null :
                Collections.unmodifiableList(extended);
        this.declarations = declarations;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public List<ExprReference> getExtendedGrammars() {
        return extended;
    }

    @Nonnull
    public DeclList getDeclarations() {
        return declarations;
    }
}
