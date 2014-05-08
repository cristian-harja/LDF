package ldf.compiler.phases;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.decl.DeclClass;
import ldf.compiler.ast.decl.DeclGrammar;
import ldf.compiler.ast.decl.Declaration;
import ldf.compiler.context.ParserContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;
import ldf.compiler.util.Util;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * @author Cristian Harja
 */
public final class Check_ExtendsCycles {
    private Check_ExtendsCycles() {
    }

    public static void checkExtends(
            @Nonnull NsNode globalNs
    ) {
        reportExtendsCycles(grammarExtendsMap(globalNs));
        reportExtendsCycles(classExtendsMap(globalNs));
    }

    private static Multimap<DeclGrammar, DeclGrammar> grammarExtendsMap(
            @Nonnull NsNode globalNS
    ) {
        Iterator<NsNode> it = globalNS.findAllByType(NsNodeType.GRAMMAR);
        Multimap<DeclGrammar, DeclGrammar> mm = ArrayListMultimap.create();

        while (it.hasNext()) {
            NsNode node = it.next();
            DeclGrammar g = (DeclGrammar) node.getAstNode();

            for (Reference r : g.getExtendedGrammars()) {
                NsNode ex = r.getReferencedNsNode();
                if (ex == null) continue;
                if (ex.getType() != NsNodeType.GRAMMAR) continue;
                mm.put(g, (DeclGrammar) ex.getAstNode());
            }
        }

        return mm;
    }

    private static Multimap<DeclClass, DeclClass> classExtendsMap(
            @Nonnull NsNode globalNS
    ) {
        Iterator<NsNode> it = globalNS.findAllByType(NsNodeType.CLASS);
        Multimap<DeclClass, DeclClass> mm = ArrayListMultimap.create();

        while (it.hasNext()) {
            NsNode node = it.next();
            DeclClass c = (DeclClass) node.getAstNode();
            Reference r = c.getSuperClass();
            if (r == null) continue;

            NsNode ex = r.getReferencedNsNode();
            if (ex == null) continue;
            if (ex.getType() != NsNodeType.CLASS) continue;
            mm.put(c, (DeclClass) ex.getAstNode());
        }

        return mm;
    }

    private static <T extends Declaration> void reportExtendsCycles(
            Multimap<T, T> mm
    ) {
        for (T d : mm.keySet()) {
            if (Util.detectCycle(mm, d)) {
                ParserContext ctx = d.getParserContext();
                ctx.reportError(d, ctx.i18n().getString(
                        "extends.cyclic_dependency"
                ), d.getDeclaredSymbolName());
            }
        }
    }


}
