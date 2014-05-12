package ldf.compiler.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.bnf.BnfSyntax;
import ldf.compiler.ast.decl.DeclClass;
import ldf.compiler.ast.decl.DeclGrammar;
import ldf.compiler.ast.decl.DeclNonTerminal;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.context.CompilerContext;
import ldf.compiler.semantics.ags.AgsNodeUnion;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;
import ldf.compiler.semantics.types.DataType;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

import static com.google.common.collect.Iterables.getOnlyElement;
import static ldf.compiler.semantics.symbols.NsNodeType.*;

/**
 * @author Cristian Harja
 */
@NotThreadSafe
public abstract class ParserGeneratorBase {

    private CompilerContext ctx;
    private String grammarName;
    private NsNode nsGrammar;
    private DeclGrammar declGrammar;

    private final Collection<NsNode> ntermList;
    private final Collection<NsNode> classList;
    private final Collection<DeclNonTerminal> ntermList2;
    private final Collection<DeclClass> classList2;
    private final Multimap<String, DataType> ntermTypes;
    private final Multimap<String, BnfSyntax> ntermSyntax;
    private final Map<String, AgsNodeUnion> ntermSyntax2;

    private final Collection<DeclClass> classListReadOnly;
    private final Map<String, AgsNodeUnion> ntermSyntaxReadOnly;

    {
        ntermList = new LinkedHashSet<NsNode>();
        classList = new LinkedHashSet<NsNode>();
        ntermList2 = new LinkedHashSet<DeclNonTerminal>();
        classList2 = new LinkedHashSet<DeclClass>();

        ntermTypes = HashMultimap.create();
        ntermSyntax = ArrayListMultimap.create();
        ntermSyntax2 = new LinkedHashMap<String, AgsNodeUnion>();

        ntermSyntaxReadOnly = Collections.unmodifiableMap(ntermSyntax2);
        classListReadOnly = Collections.unmodifiableCollection(classList2);
    }

    public ParserGeneratorBase() {
    }

    public final boolean compile(
            String grammarName,
            CompilerContext ctx
    ) throws Exception {
        this.grammarName = grammarName;
        this.ctx = ctx;
        nsGrammar = resolveGrammar(grammarName);
        if (nsGrammar == null) {
            declGrammar = null;
        } else {
            declGrammar = (DeclGrammar) nsGrammar.getAstNode();
        }

        findDeclarations();

        if (!validateNtermTypes()) return false;

        agsMakeUnions();

        doCompile();

        return true;

    }

    protected abstract void doCompile() throws Exception;

    public Map<String, AgsNodeUnion> getNtermSyntax() {
        return ntermSyntaxReadOnly;
    }

    public Collection<DeclClass> getClassList() {
        return classListReadOnly;
    }

    @Nullable
    private NsNode resolveGrammar(String name) {
        String[] path = name.split("\\.");
        NsNode ns = ctx.getGlobalNamespace();
        for (int i = 0, n = path.length; i < n; i++) {
            String s = path[i];
            Multimap<NsNodeType, NsNode> mm = ns.getChildren().get(s);
            if (mm == null) return null;
            boolean isLast = i == n - 1;
            ns = getOnlyElement(mm.get(
                    isLast ? GRAMMAR : PACKAGE
            ));
            if (ns == null) return null;
        }
        return ns;
    }

    private void findDeclarations() {
        findDeclarations(nsGrammar, declGrammar);
        for (NsNode ns : ntermList) {
            ntermList2.add((DeclNonTerminal) ns.getAstNode());
        }
        for (NsNode ns : classList) {
            classList2.add((DeclClass) ns.getAstNode());
        }
    }

    private void findDeclarations(NsNode ns, DeclGrammar g) {
        ntermList.addAll(ns.getChildren(NTERM));
        classList.addAll(ns.getChildren(CLASS));

        for (Reference ref : g.getExtendedGrammars()) {
            ns = ref.getReferencedNsNode();
            assert ns != null;
            findDeclarations(ns, (DeclGrammar) ns.getAstNode());
        }
    }

    private boolean validateNtermTypes() {
        boolean valid = true;
        for (DeclNonTerminal nterm : ntermList2) {
            String name = nterm.getId().getName();
            if (nterm.getSyntax() != null) {
                ntermSyntax.put(name, nterm.getSyntax());
            }
            TypeExpression expr = nterm.getType();
            if (expr == null) continue;
            DataType t = expr.getDataType();
            if (t == null) continue;
            ntermTypes.put(name, t);
        }
        for (String key : ntermTypes.keySet()) {
            if (ntermTypes.get(key).size() > 1) {
                ctx.reportError(grammarName, null, ctx.i18n().getString(
                        "nterm.type_mismatch"
                ), key);
                valid = false;
            }
            if (ntermSyntax.get(key).isEmpty()) {
                ctx.reportError(grammarName, null, ctx.i18n().getString(
                        "nterm.no_body"
                ), key);
                valid = false;
            }
        }
        return valid;
    }

    private void agsMakeUnions() {
        for (String nterm : ntermSyntax.keySet()) {
            ntermSyntax2.put(nterm, new AgsNodeUnion(
                    ntermSyntax.get(nterm)
            ));
        }
    }

    public String getGrammarName() {
        return grammarName;
    }
}
