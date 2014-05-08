package ldf.compiler.phases;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.ImportList;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.decl.DeclClass;
import ldf.compiler.ast.decl.DeclGrammar;
import ldf.compiler.ast.decl.DeclVariable;
import ldf.compiler.context.CompilerContext;
import ldf.compiler.context.ParserContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;
import ldf.compiler.semantics.symbols.Scope;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Populates {@link Scope} objects with imported symbols (via the {@code
 * import} statement or from extended grammars).
 *
 * @author Cristian Harja
 */
public final class Phase_ResolveImports {

    private Phase_ResolveImports() {
    }

    public static void resolveImports(
            @Nonnull CompilerContext ctx,
            @Nonnull AstSourceFile file
    ) {
        resolveImports0(ctx, file);
        resolveGrammarExtends(file);
        resolveClassExtends(file);
    }

    private static void resolveImports0(
            @Nonnull CompilerContext ctx,
            @Nonnull AstSourceFile file
    ) {
        Scope globalScope = ctx.getGlobalScope();
        Scope fileScope = file.getScope();
        fileScope.importAll(file.getPackageNsNode());

        for (ImportList.Entry e : file.getImportList().getEntries()) {
            boolean[] usedWildcard = new boolean[1];
            NsNode node = globalScope.resolveStatic(
                    e.getImportRef(), true, usedWildcard
            );
            if (node == null) {
                continue;
            }
            e.setReferencedNsNode(node);
            if (usedWildcard[0]) {
                fileScope.importAll(node);
            } else {
                // TODO: use id instead of string
                AstIdentifier id = e.getImportAs();
                fileScope.importOne(node,
                        id != null ? id.getName() : null
                );
            }
        }
    }

    private static void resolveGrammarExtends(
            AstSourceFile file
    ) {
        Iterator<DeclGrammar> it = file.findAllOfType(DeclGrammar.class);
        while (it.hasNext()) {
            DeclGrammar grammar = it.next();
            for (Reference r : grammar.getExtendedGrammars()) {
                NsNode n = r.resolveAsGrammar();
                if (n == null) continue;
                grammar.getScope().importAll(n);
            }
        }
    }

    private static Set<DeclClass> getAllExtendedClasses(DeclClass cls) {
        Set<DeclClass> set = new HashSet<DeclClass>();
        DeclClass c = cls;
        do {
            c = c.getSuperClassDecl();
        } while (c!= null && set.add(c));
        return set;
    }

    private static void resolveClassExtends (
            AstSourceFile file
    ){
        Iterator<DeclClass> it = file.findAllOfType(DeclClass.class);
        loop: while (it.hasNext()) {
            DeclClass declClass = it.next();
            NsNode c = declClass.getDeclaredNsNode();
            if (c == null) continue;
            Reference r = declClass.getSuperClass();
            if (r == null) continue;
            NsNode n = r.resolveAsClass();
            if (n == null) continue;

            Map<String, DeclVariable> fields;
            fields = new LinkedHashMap<String, DeclVariable>();

            Collection<DeclClass> extended;
            extended = getAllExtendedClasses(declClass);

            for (DeclClass cls : extended) {
                if (cls == declClass) {
                    // cyclic dependency detected
                    continue loop;
                }
                NsNode c2 = cls.getDeclaredNsNode();
                if (c2 == null) continue;
                for (NsNode f : c2.getChildren(NsNodeType.VARIABLE)) {
                    fields.put(f.getName(), (DeclVariable) f.getAstNode());
                }
            }

            for (NsNode f : c.getChildren(NsNodeType.VARIABLE)) {
                if (fields.containsKey(f.getName())) {
                    AstIdentifier id = f.getIdentifier();
                    ParserContext ctx = id.getParserContext();
                    ctx.reportError(id, ctx.i18n().getString(
                            "extends.clashing_field"
                    ), id.getName());
                } else {
                    c.declareChild(
                            f.getIdentifier(),
                            f.getType(),
                            f.getAstNode()
                    );
                }
            }

        }
    }


}
