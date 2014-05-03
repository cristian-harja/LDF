package ldf.compiler.phases;

import com.google.common.collect.Multimap;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.context.ParserContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

/**
 * Runs after {@link Phase_CollectDeclarations} to detect conflicting
 * declarations.
 *
 * @author Cristian Harja
 */
public final class Check_DeclaredSymbols {
    private Check_DeclaredSymbols() {
    }

    public static void checkSymbols(@Nonnull NsNode nsNode) {
        Map<String, Multimap<NsNodeType, NsNode>> children;
        children = nsNode.getChildren();

        for (Multimap<NsNodeType, NsNode> mm : children.values()) {
            Collection<NsNodeType> types = mm.keySet();
            for (NsNodeType t1 : types) {
                Collection<NsNode> nodes = mm.get(t1);
                if (!nsNode.getType().canContain(t1)) {
                    reportCantContain(nodes, nsNode);
                }
                if (nodes.size() > 1) {
                    reportDuplicates(nodes);
                }
                for (NsNodeType t2 : types) {
                    if (t1.ordinal() <= t2.ordinal()) continue;
                    if (t2.clashesWith(t1)) {
                        reportClashes(nodes, t2);
                    }
                }
                for (NsNode node : nodes) {
                    checkSymbols(node);
                }
            }
        }
    }

    private static void reportDuplicates(Collection<NsNode> nodes) {
        for (NsNode node : nodes) {
            AstIdentifier id = node.getIdentifier();
            ParserContext ctx = id.getParserContext();
            ctx.reportError(id, ctx.i18n().getString(
                    "declaration.duplicate"
            ), node.getType(), id.getName());
        }
    }

    private static void reportClashes(
            Collection<NsNode> nodes,
            NsNodeType type
    ) {
        for (NsNode node : nodes) {
            AstIdentifier id = node.getIdentifier();
            ParserContext ctx = id.getParserContext();
            ctx.reportError(id, ctx.i18n().getString(
                    "declaration.clash"
            ), id.getName(), type.toString());
        }
    }

    private static void reportCantContain(
            Collection<NsNode> nodes,
            NsNode parentNode
    ) {
        for (NsNode node : nodes) {
            AstIdentifier id = node.getIdentifier();
            ParserContext ctx = id.getParserContext();
            ctx.reportError(id,
                    ctx.i18n().getString("declaration.cant_contain"),
                    node.getType(), id.getName(),
                    parentNode.getType().toString(),
                    parentNode.getName()
            );
        }
    }

}
