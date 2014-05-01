package ldf.compiler.semantics.symbols;

import ldf.compiler.ast.decl.DeclVariable;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class FieldNode extends NsNode {

    @Nonnull
    private final DeclVariable astNode;

    public FieldNode(
            NsNodeType type, NsNode parent, String name,
            @Nonnull DeclVariable astNode
    ) {
        super(type, parent, name);
        this.astNode = astNode;
    }

    @Nonnull
    public DeclVariable getAstNode() {
        return astNode;
    }
}
