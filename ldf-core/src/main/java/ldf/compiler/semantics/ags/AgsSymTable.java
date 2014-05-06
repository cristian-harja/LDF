package ldf.compiler.semantics.ags;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.bnf.BnfAlternation;
import ldf.compiler.ast.bnf.BnfAtom;
import ldf.compiler.ast.bnf.BnfUnion;
import ldf.compiler.context.ParserContext;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * <p>This class walks through a network of {@link AgsSymbol}s in order to
 * determine the list of symbols which are accessible to grammar actions.
 * </p>
 *
 * @author Cristian Harja
 */
final class AgsSymTable {
    Stack<StackFrame> stack;

    Snapshot snapshot() {
        StackFrame sf = stack.peek();
        return new Snapshot(sf.precedingSymbols, sf.currentSymbols);
    }

    private class StackFrame {
        boolean hasActions;
        Map<String, AgsSymbol> precedingSymbols;
        Map<String, AgsSymbol> currentSymbols;

        private StackFrame() {
            precedingSymbols = new LinkedHashMap<String, AgsSymbol>();
            currentSymbols = new LinkedHashMap<String, AgsSymbol>();
        }
    }

    AgsSymTable() {
        stack = new Stack<StackFrame>();
        stack.push(new StackFrame());
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
                        iterateCurrentSymbols();

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
                    case REFERENCE:
                        // TODO
                        // (mostly type checking, at this point)
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

    }

    private void beginItem(AgsNode node) {
        if (node.hasLabels() || node.hasQuantifiers()) {
            push();
        }
    }

    private void endItem(AgsNode node) {
        if (node.hasLabels() || node.hasQuantifiers()) {
            if (node.hasQuantifiers()) {
                iterateCurrentSymbols();
            }
            StackFrame sf = stack.pop();
            for (AstIdentifier label : node.labels()) {
                add(label, node, sf);
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
            add(AgsSymbol.makeUnion(mm.get(label)));
        }
    }

    private void push() {
        StackFrame s1 = stack.peek();
        StackFrame s2 = new StackFrame();
        s2.precedingSymbols.putAll(s1.precedingSymbols);
        s2.precedingSymbols.putAll(s1.currentSymbols);
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

    private void add(AstIdentifier label, AgsNode node, StackFrame sf) {
        AgsSymbol e = new AgsSymbol();
        e.agsNode = node;
        e.label = label;
        e.originalSymbol = e;
        if (sf != null) {
            if (!sf.currentSymbols.isEmpty() && !sf.hasActions) {
                e.nestedSymbols = new LinkedHashMap<String, AgsSymbol>(
                        sf.currentSymbols
                );
                e.nestedSymbolsReadOnly = Collections.unmodifiableMap(
                        e.nestedSymbols
                );
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
    }

}

