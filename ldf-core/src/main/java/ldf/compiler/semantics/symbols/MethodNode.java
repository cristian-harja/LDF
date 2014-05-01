package ldf.compiler.semantics.symbols;

import ldf.parser.ast.decl.DeclFunction;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class MethodNode extends NsNode {
    @Nonnull
    private final DeclFunction astNode;

    public MethodNode(
            NsNodeType type, NsNode parent, String name,
            @Nonnull DeclFunction astNode
    ) {
        super(type, parent, name);
        this.astNode = astNode;
    }

    @Nonnull
    public DeclFunction getAstNode() {
        return astNode;
    }
}
