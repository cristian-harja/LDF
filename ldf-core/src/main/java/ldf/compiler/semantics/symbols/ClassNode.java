package ldf.compiler.semantics.symbols;

import ldf.parser.ast.decl.DeclClass;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class ClassNode extends NsNode {

    @Nonnull
    private final DeclClass astNode;

    public ClassNode(
            NsNodeType type, NsNode parent, String name,
            @Nonnull DeclClass astNode
    ) {
        super(type, parent, name);
        this.astNode = astNode;
    }

    @Nonnull
    public DeclClass getAstNode() {
        return astNode;
    }
}
