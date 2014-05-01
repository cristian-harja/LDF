package ldf.compiler.semantics.symbols;

import ldf.parser.ast.decl.DeclNonTerminal;

/**
 * @author Cristian Harja
 */
public final class NtermNode extends NsNode {

    private final DeclNonTerminal astNode;

    public NtermNode(
            NsNodeType type, NsNode parent, String name,
            DeclNonTerminal astNode
    ) {
        super(type, parent, name);
        this.astNode = astNode;
    }

    public DeclNonTerminal getAstNode() {
        return astNode;
    }
}
