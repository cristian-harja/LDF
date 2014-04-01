package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.expr.ExprReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;

/**
 * Declaration of a <em>grammar</em>. Backed by the {@code decl_grammar}
 * non-terminal.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclGrammar extends Declaration {

    @Nonnull
    private AstIdentifier name;

    @Nullable
    private List<ExprReference> extended;

    @Nonnull
    private DeclList declarations;

    /**
     * @param name the name of the grammar
     * @param extended the extended grammars
     * @param declarations the contents of the grammar
     */
    public DeclGrammar(
            @Nonnull AstIdentifier name,
            @Nullable List<ExprReference> extended,
            @Nonnull DeclList declarations
    ) {
        this.name = name;
        this.extended = extended == null ? null :
                Collections.unmodifiableList(extended);
        this.declarations = declarations;

        addAstChildren(name);
        addAstChildren(extended);
        addAstChildren(declarations);
    }

    @Nonnull
    public AstIdentifier getName() {
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
