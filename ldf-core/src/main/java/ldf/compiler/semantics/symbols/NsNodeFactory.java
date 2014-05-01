package ldf.compiler.semantics.symbols;

import ldf.parser.ast.AstNode;
import ldf.parser.ast.decl.*;

import javax.annotation.Nonnull;

/**
 * @author Cristian Harja
 */
public final class NsNodeFactory {

    @Nonnull
    static NsNode create(
            NsNodeType type,
            NsNode parent,
            String name,
            AstNode astNode
    ) {
        NsNode node;
        switch (type) {
            case PACKAGE:
                node = new PackageNode(type, parent, name);
                break;

            case GRAMMAR:
                node = new GrammarNode(
                        type, parent, name,
                        (DeclGrammar) astNode
                );
                break;

            case NTERM:
                node = new NtermNode(
                        type, parent, name,
                        (DeclNonTerminal) astNode
                );
                break;

            case CLASS:
                node = new ClassNode(
                        type, parent, name,
                        (DeclClass) astNode
                );
                break;

            case METHOD:
                node = new MethodNode(
                        type, parent, name,
                        (DeclFunction) astNode
                );
                break;

            case FIELD:
                node = new FieldNode(
                        type, parent, name,
                        (DeclVariable) astNode
                );
                break;

            default:
                throw new IllegalArgumentException(
                        "Factory method does not accept " + type
                );
        }

        NsNode gotParent = node.getParent();
        String gotName = node.getName();

        if (gotParent != parent ||
            node.getType() != type ||
            gotName != null && !gotName.equals(name) ||
            name != null && gotName == null
        ) {
            throw new RuntimeException(
                    "super() not called with the correct arguments"
            );
        }

        return node;
    }

}
