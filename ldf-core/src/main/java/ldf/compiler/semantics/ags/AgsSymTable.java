package ldf.compiler.semantics.ags;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.bnf.BnfAlternation;
import ldf.compiler.ast.bnf.BnfAtom;
import ldf.compiler.ast.bnf.BnfSyntax;
import ldf.compiler.ast.bnf.BnfUnion;
import ldf.compiler.ast.decl.DeclNonTerminal;
import ldf.compiler.ast.decl.DeclWhereClause;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.context.CompilerContext;
import ldf.compiler.context.ParserContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;
import ldf.compiler.semantics.types.DataType;
import ldf.compiler.semantics.types.NoType;
import ldf.compiler.semantics.types.ObjectType;
import ldf.compiler.semantics.types.TypeEnv;

import java.util.*;

import static com.google.common.collect.Iterables.getOnlyElement;
import static ldf.compiler.ast.decl.DeclWhereClause.LabelTypeEntry;

/**
 * <p>This class walks through a network of {@link AgsSymbol}s in order to
 * determine the list of symbols which are accessible to grammar actions.
 * </p>
 *
 * @author Cristian Harja
 */
public final class AgsSymTable {
    Stack<StackFrame> stack;

    DeclNonTerminal nterm;
    DeclWhereClause whereClause;
    BnfSyntax bnfSyntax;
    AgsNode agsRoot;

    TypeEnv typeEnv;

    Snapshot snapshot() {
        StackFrame sf = stack.peek();
        return new Snapshot(sf.precedingSymbols, sf.currentSymbols);
    }

    private class StackFrame {
        boolean hasActions;
        Map<String, AgsSymbol> precedingSymbols;
        Map<String, AgsSymbol> currentSymbols;

        Multimap<String, DataType> expectedSymbolTypes;
        Multimap<String, LabelTypeEntry> currentTypeList;
        int currentTypeListDepth;

        private StackFrame() {
            precedingSymbols = new LinkedHashMap<String, AgsSymbol>();
            currentSymbols = new LinkedHashMap<String, AgsSymbol>();
            currentTypeList = ImmutableListMultimap.of();
        }
    }

    public AgsSymTable(CompilerContext ctx, DeclNonTerminal nterm) {

        StackFrame sf = new StackFrame();
        stack = new Stack<StackFrame>();
        stack.push(sf);

        sf.currentTypeList = ArrayListMultimap.create();

        this.nterm = nterm;
        whereClause = nterm.getWhereClause();
        bnfSyntax = nterm.getSyntax();
        typeEnv = ctx.getTypeEnvironment();

        if (bnfSyntax != null) {
            agsRoot = bnfSyntax.getAgsRoot();
        }

        if (whereClause != null) {
            for (LabelTypeEntry e : whereClause.getLabelList()) {
                sf.currentTypeList.put(
                        e.getLabel().getPath().get(0).getName(), e
                );
            }
        }

    }

    public void initSymbols() {
        if (agsRoot == null) return;
        initSymbols(agsRoot);
    }

    void initSymbols(AgsNode node) {
        Multimap<String, AgsSymbol> union;
        switch (node.getType()) {
            case CONCAT:
                beginItem(node);
                for (AgsNode n : node) {
                    initSymbols(n);
                }
                endItem(node);
                break;

            case UNION:
                beginItem(node);
                union = ArrayListMultimap.create(); // begin union
                for (AgsNode n : node) {
                    beginUnionBranch();
                    initSymbols(n);
                    endUnionBranch(union);
                }
                endUnion(union); // end union (get symbols from multi-map)
                endItem(node);
                break;

            case ITEM:
                BnfAtom atom = node.getAtom();
                assert atom != null;
                beginItem(node);
                switch (atom.getBnfAtomType()) {
                    case ALTERNATION:

                        // as far as symbols and types are concerned,
                        // an alternation can be emulated by a union:
                        // [e1, e2]+ behaves just like to ((e1|e2)+)

                        push();
                        union = ArrayListMultimap.create(); // begin union

                        BnfAlternation alt = (BnfAlternation) atom;
                        BnfUnion element = alt.getElementSyntax();
                        BnfUnion separator = alt.getSeparatorSyntax();
                        AgsNode e1 = element.getAbstractGrammarSpec();
                        AgsNode e2 = separator.getAbstractGrammarSpec();

                        beginUnionBranch();
                        initSymbols(e1);
                        endUnionBranch(union);

                        beginUnionBranch();
                        initSymbols(e2);
                        endUnionBranch(union);

                        endUnion(union); // end union

                        pop();

                        break;

                    case ACTION:
                    case PLACEHOLDER:
                    case GUARD:
                        stack.peek().hasActions = true;
                        Snapshot snap = snapshot();
                        AgsNode agsNode = atom.getAbstractGrammarSpec();
                        agsNode.snapshot = snap;
                        break;

                    case LITERAL_CHAR:
                    case LITERAL_STRING:
                        node.dataType = typeEnv.getStringDataType();
                        break;

                    case REFERENCE:
                        assert atom instanceof Reference;
                        Reference ref = (Reference) atom;
                        NsNode n = ref.getReferencedNsNode();
                        if (n == null) {
                            node.dataType = NoType.INSTANCE;
                        } else {
                            AstNode astNode = n.getAstNode();
                            DeclNonTerminal nterm;
                            assert n.getType() == NsNodeType.NTERM;
                            assert astNode != null;
                            assert astNode instanceof DeclNonTerminal;
                            nterm = (DeclNonTerminal) astNode;

                            TypeExpression type = nterm.getType();

                            if (type == null) {
                                node.dataType = null;
                            } else {
                                node.dataType = type.getDataType();
                            }

                        }
                        break;

                    default:
                        // case CONCATENATION:
                        // case UNION:
                        throw new RuntimeException(
                                "Unexpected BnfAtomType." + node.getType()
                        );
                }
                endItem(node);
        }
        node.snapshot = snapshot();

    }

    private void beginItem(AgsNode node) {
        if (node.hasLabels() || node.hasQuantifiers()) {

            StackFrame sf1, sf2;
            Multimap<String, DataType> targets;
            Multimap<String, LabelTypeEntry> types;
            Multimap<String, LabelTypeEntry> nested;
            int depth;

            sf1 = stack.peek();
            push();
            sf2 = stack.peek();

            if (!node.hasLabels()) return;

            types = sf1.currentTypeList;
            depth = sf1.currentTypeListDepth;

            if (types.isEmpty()) return;

            // symbols nested under the label currently being defined
            nested = ArrayListMultimap.create();

            // user-supplied types info (should be only one) for this label
            targets = ArrayListMultimap.create();

            // iterate through each label this node has...
            for (AstIdentifier label : node.labels()) {

                // if there is any type info for that label...
                if (types.containsKey(label.getName())) {

                    // now get the bits of info under the current label
                    // (there may be several entries, some targeting this
                    // node, some targeting nested labels)

                    String name = label.getName();
                    Collection<LabelTypeEntry> entries = types.get(name);

                    // for each bit of information under this label
                    for (LabelTypeEntry entry : entries) {

                        // look at the full path of this entry
                        List<AstIdentifier> r = entry.getLabel().getPath();

                        // is this the last identifier in that path?
                        if (r.size() == depth + 1) {
                            // the type information refers to this label
                            DataType dt = entry.getType().getDataType();
                            targets.put(name, dt);
                        } else {
                            // the path refers to a nested label;
                            // map the next element in the path
                            String nextName = r.get(depth).getName();
                            nested.put(nextName, entry);
                        }
                    }
                }
            }

            sf2.expectedSymbolTypes = targets;
            sf2.currentTypeList = nested;
            sf2.currentTypeListDepth = depth + 1;

        }
    }

    private void endItem(AgsNode node) {
        if (node.hasLabels() || node.hasQuantifiers()) {
            if (node.hasQuantifiers()) {
                iterateCurrentSymbols();
            }
            StackFrame sf = stack.pop();
            DataType expectedType = null;
            if (sf.expectedSymbolTypes != null) {
                if (sf.expectedSymbolTypes.size() > 1) {
                    AstNode astNode = node.getAstNode();
                    if (astNode != null) {
                        ParserContext ctx = astNode.getParserContext();
                        ctx.reportError(astNode, ctx.i18n().getString(
                                "nterm.multiple_labels"
                        ));
                    }
                } else if (!sf.expectedSymbolTypes.isEmpty()) {
                    expectedType = getOnlyElement(
                            sf.expectedSymbolTypes.values()
                    );
                }
            }
            if (node.hasLabels()) {
                // nest labels
                for (AstIdentifier label : node.labels()) {
                    AgsSymbol s = add(label, node, sf);
                    s.expectedType = expectedType;
                    s.getActualType();
                }
            } else {
                for (AgsSymbol s : sf.currentSymbols.values()) {
                    s.expectedType = expectedType;
                    add(s);
                    s.getActualType();
                }
            }
        }
    }

    private void beginUnionBranch() {
        push();
    }

    private void endUnionBranch(Multimap<String, AgsSymbol> mm) {
        for (Map.Entry<String, AgsSymbol> e :
                stack.pop().currentSymbols.entrySet()) {
            mm.put(e.getKey(), e.getValue());
        }
    }

    private void endUnion(Multimap<String, AgsSymbol> mm) {
        for (String label : mm.keySet()) {
            add(AgsSymbol.makeUnion(typeEnv, mm.get(label)));
        }
    }

    private void push() {
        StackFrame s1 = stack.peek();
        StackFrame s2 = new StackFrame();
        s2.precedingSymbols.putAll(s1.precedingSymbols);
        s2.precedingSymbols.putAll(s1.currentSymbols);

        s2.currentTypeList = s1.currentTypeList;
        s2.currentTypeListDepth = s1.currentTypeListDepth;

        stack.push(s2);
    }

    private void pop() {
        StackFrame sf = stack.pop();
        for (AgsSymbol s : sf.currentSymbols.values()) {
            add(s);
        }
    }

    private void iterateCurrentSymbols() {
        for (Map.Entry<String, AgsSymbol> e :
                stack.peek().currentSymbols.entrySet()) {
            e.setValue(AgsSymbol.makeIteration(e.getValue()));
        }
    }

    private void add(AgsSymbol e) {
        stack.peek().currentSymbols.put(e.label.getName(), e);
    }

    private AgsSymbol add(AstIdentifier label, AgsNode node, StackFrame sf) {
        AgsSymbol e = new AgsSymbol();
        e.agsNode = node;
        e.label = label;
        e.originalSymbol = e;
        e.deductedType = node.dataType;
        if (sf != null) {
            if (!sf.currentSymbols.isEmpty() && !sf.hasActions) {
                e.nestedSymbols = new LinkedHashMap<String, AgsSymbol>(
                        sf.currentSymbols
                );
                e.nestedSymbolsReadOnly = Collections.unmodifiableMap(
                        e.nestedSymbols
                );

                ObjectType.Builder objType = new ObjectType.Builder();
                for (AgsSymbol s : e.nestedSymbols.values()) {
                    objType.add(s.getLabel().getName(), s.getActualType());
                }
                e.deductedType = objType.build();

            }
        }

        // report existing label
        String name = label.getName();
        StackFrame top = stack.peek();
        if (top.precedingSymbols.containsKey(name)) {
            ParserContext ctx = label.getParserContext();
            ctx.reportError(label, ctx.i18n().getString(
                    "declaration.duplicate"
            ), "LABEL", name);
        } else if (top.currentSymbols.containsKey(name)) {
            ParserContext ctx = label.getParserContext();
            ctx.reportError(label, ctx.i18n().getString(
                    "declaration.duplicate"
            ), "LABEL", name);
        }

        // add new label
        top.currentSymbols.put(label.getName(), e);
        return e;
    }

}

