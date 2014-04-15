package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.Reference;

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
    private AstIdentifier id;

    @Nonnull
    private List<Reference> extended;

    @Nonnull
    private DeclList declarations;

    /**
     * @param name the name of the grammar
     * @param extended the extended grammars
     * @param declarations the contents of the grammar
     */
    public DeclGrammar(
            @Nonnull AstIdentifier name,
            @Nullable List<Reference> extended,
            @Nonnull DeclList declarations
    ) {
        this.id = name;
        this.extended = extended == null
                ? Collections.<Reference>emptyList()
                : Collections.unmodifiableList(extended);
        this.declarations = declarations;

        addAstChildren(name);
        addAstChildren(extended);
        addAstChildren(declarations);
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    @Nonnull
    public List<Reference> getExtendedGrammars() {
        return extended;
    }

    @Nonnull
    public DeclList getDeclarations() {
        return declarations;
    }
}
