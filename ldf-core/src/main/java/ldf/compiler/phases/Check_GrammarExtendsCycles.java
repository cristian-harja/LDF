package ldf.compiler.phases;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.decl.DeclGrammar;
import ldf.compiler.context.ParserContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;
import ldf.compiler.semantics.symbols.Scope;
import ldf.compiler.util.Util;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * @author Cristian Harja
 */
public final class Check_GrammarExtendsCycles {
    private Check_GrammarExtendsCycles() {
    }

    public static void checkExtends(
            @Nonnull NsNode globalNs
    ) {
        reportExtendsCycles(grammarExtendsMap(globalNs));
    }

    private static Multimap<DeclGrammar, DeclGrammar> grammarExtendsMap(
            @Nonnull NsNode globalNS
    ) {
        Iterator<NsNode> it = globalNS.findAllByType(NsNodeType.GRAMMAR);
        Multimap<DeclGrammar, DeclGrammar> mm = ArrayListMultimap.create();

        while (it.hasNext()) {
            NsNode node = it.next();
            DeclGrammar g = (DeclGrammar) node.getAstNode();
            Scope s = g.getScope();

            for (Reference r : g.getExtendedGrammars()) {
                NsNode ex = r.getReferencedNsNode();
                if (ex == null) continue;
                mm.put(g, (DeclGrammar) ex.getAstNode());
            }
        }

        return mm;
    }

    private static void reportExtendsCycles(
            Multimap<DeclGrammar, DeclGrammar> mm
    ) {
        for (DeclGrammar g : mm.keySet()) {
            if (Util.detectCycle(mm, g)) {
                ParserContext ctx = g.getParserContext();
                ctx.reportError(g, ctx.i18n().getString(
                        "cyclic.dependency"
                ), g.getId().getName());
            }
        }
    }


}
