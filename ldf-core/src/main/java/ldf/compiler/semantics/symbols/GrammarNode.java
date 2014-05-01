package ldf.compiler.semantics.symbols;

import ldf.compiler.ast.decl.DeclGrammar;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class GrammarNode extends NsNode {

    @Nonnull
    private final DeclGrammar astNode;

    public GrammarNode(
            NsNodeType type, NsNode parent, String name,
            @Nonnull DeclGrammar astNode
    ) {
        super(type, parent, name);
        this.astNode = astNode;
    }

    @Nonnull
    public DeclGrammar getAstNode() {
        return astNode;
    }
}
