package ldf.parser.ast.bnf;

import ldf.parser.ast.AstNode;
import ldf.parser.decl.Scope;
import ldf.parser.decl.SymbolType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Utility class, which builds a Directed Acyclic Graph (DAG) from the BNF
 * syntax. Should be used for semantic analysis and validation.
 *
 * @author Cristian Harja
 */
@ParametersAreNonnullByDefault
public final class BnfSyntaxDag {

    public final static class DagHandle {
        protected boolean invalid;
        protected Point p1, p2;
        protected Scope scopeL, scopeR;

        protected DagHandle(Node node) {
            Point p1 = new Point();
            Point p2 = new Point();
            scopeL = new Scope(null, SymbolType.NTERM_LABEL.bitMask, null);
            scopeR = scopeL;

            p1.outCount = 1;
            p1.outFirst = node;
            p1.outLast = node;
            node.inPoint = p1;

            p2.inCount = 1;
            p2.inFirst = node;
            p2.inLast = node;
            node.outPoint = p2;

            this.p1 = p1;
            this.p2 = p2;

        }

        public void assertValid() {
            if (invalid) {
                throw new IllegalStateException(
                        "Supplied DAG object is no longer valid"
                );
            }
        }
    }

    public final static class Point {
        protected int inCount, outCount;
        protected Node inFirst, inLast;
        protected Node outFirst, outLast;

        private static void unionLeft(Point p1, Point p2) {
            Node n, n1, n2;
            n1 = p1.outLast;
            n2 = p2.outFirst;
            n1.inNext = n2;
            n2.inPrev = n1;
            for (n = n2; n != null; n = n.inNext) {
                n.inPoint = p1;
            }
            p1.outCount += p2.outCount;
            p1.outLast = p2.outLast;

            p2.outCount = 0;
            p2.outFirst = null;
            p2.outLast = null;
        }

        private static void unionRight(Point p1, Point p2) {
            Node n, n1, n2;
            n1 = p1.inLast;
            n2 = p2.inFirst;
            n1.outNext = n2;
            n2.outPrev = n1;
            for (n = n2; n != null; n = n.outNext) {
                n.outPoint = p1;
            }
            p1.inCount += p2.inCount;
            p1.inLast = p2.inLast;

            p2.inCount = 0;
            p2.inFirst = null;
            p2.inLast = null;
        }

        protected static void union(DagHandle dag1, DagHandle dag2) {
            // init
            dag1.assertValid();
            dag2.assertValid();

            // join
            unionLeft(dag1.p1, dag2.p1);
            unionRight(dag1.p2, dag2.p2);

            // link the scopes
            if (dag1.scopeL.getSymbolDefs().isEmpty()) {
                dag2.scopeL.importScope(dag1.scopeL);
            } else {
                Scope s = new Scope(
                        null, SymbolType.NTERM_LABEL.bitMask, null
                );
                dag1.scopeL.importScope(s);
                dag2.scopeL.importScope(s);
                dag1.scopeL = s;
            }

            if (dag1.scopeR.getSymbolDefs().isEmpty()) {
                dag2.scopeR.importScope(dag1.scopeR);
                dag1.scopeR = dag2.scopeR;
            } else {
                Scope s = new Scope(
                        null, SymbolType.NTERM_LABEL.bitMask, null
                );
                dag1.scopeR.importScope(s);
                dag2.scopeR.importScope(s);
                dag1.scopeR = s;
            }

            // discard `dag2`
            dag2.invalid = true;
        }

        protected static void chain(DagHandle dag1, DagHandle dag2) {
            Point p1, p2;

            // init
            dag1.assertValid();
            dag2.assertValid();
            p1 = dag1.p2;
            p2 = dag2.p1;

            p1.outFirst = p2.outFirst;
            p1.outLast = p2.outLast;
            p1.outCount = p2.outCount;
            for (Node n = p1.outFirst; n != null; n = n.inNext) {
                n.inPoint = p1;
            }

            // link scopes
            dag2.scopeL.importScope(dag1.scopeR);
            dag1.scopeR = dag2.scopeR;

            // discard `dag2`
            dag1.p2 = dag2.p2;
            dag2.invalid = true;
        }

        protected static void removeDuplicateEmptySymbols(DagHandle dag) {
            Point p1, p2;
            Node n, empty = null;
            dag.assertValid();
            p1 = dag.p1;
            p2 = dag.p2;
            for (n = p1.outFirst; n != null; n = n.inNext) {
                if (n.outPoint == p2 && n instanceof EmptyNode) {
                    if (empty == null) {
                        empty = n;
                    } else {
                        p1.outCount--;
                        p2.inCount--;

                        // if (n.inPrev != null) is always true
                        // because empty = a previous node (!= null)
                        n.inPrev.inNext = n.inNext;
                        n.outPrev.outNext = n.outNext;

                        if (p1.outLast == n) {
                            p1.outLast = n.inPrev;
                        } else {
                            n.inNext.inPrev = n.inPrev;
                            n.outNext.outPrev = n.outPrev;
                        }
                    }
                }
            }
        }
    }

    public final static class EmptyNode extends Node {
    }

    public final static class Atom extends Node {
        protected BnfAtom atom;

        public Atom(BnfAtom atom) {
            this.atom = atom;
        }
    }

    public final static class Quantifier extends Node {
        protected BnfQuantifier quantifier;
        protected DagHandle dag;

        public Quantifier(BnfQuantifier quantifier, DagHandle dag) {
            this.quantifier = quantifier;
            this.dag = dag;
        }
    }

    public static abstract class Node {
        protected Point inPoint, outPoint;
        protected Node inPrev, inNext;
        protected Node outPrev, outNext;
        protected Node() {}
    }

    public static DagHandle buildDag(BnfSyntax bnf) {
        return buildDag(bnf.getRoot());
    }

    private static DagHandle buildDag(BnfUnion bnf) {
        List<BnfConcat> items = bnf.getItems();
        if (items.isEmpty()) {
            return new DagHandle(new EmptyNode());
        } else if (items.size() == 1) {
            return buildDag(items.get(0));
        } else {
            DagHandle result = null;
            for (BnfConcat item : items) {
                DagHandle dag = buildDag(item);
                if (result == null) {
                    result = dag;
                } else {
                    Point.union(result, dag);
                }
            }
            Point.removeDuplicateEmptySymbols(result);
            return result;
        }
    }

    private static DagHandle buildDag(BnfConcat bnf) {
        List<BnfItem> items = bnf.getItems();
        if (items.isEmpty()) {
            return new DagHandle(new EmptyNode());
        } else if (items.size() == 1) {
            return buildDag(items.get(0));
        } else {
            DagHandle result = null;
            for (BnfItem item : items) {
                DagHandle dag = buildDag(item);
                if (result == null) {
                    result = dag;
                } else {
                    Point.chain(result, dag);
                }
            }
            return result;
        }
    }

    private static DagHandle buildDag(BnfItem bnf) {
        BnfAtom atom = bnf.getAtom();
        DagHandle dag = buildDag(bnf.getAtom());

        BnfLabel label = bnf.getLabel();
        if (label != null) {
            dag.scopeL.defineSymbol(label.getId(), SymbolType.NTERM_LABEL);
        }

        BnfQuantifier q = bnf.getQuantifier();
        if (q != null) {
            DagHandle newHandle = new DagHandle(new Quantifier(q, dag));
            newHandle.scopeL = dag.scopeL;
            newHandle.scopeR = dag.scopeR;
            dag = newHandle;
        }

        AstNode node = (AstNode) atom;
        if (node.hasOwnScope()) {
            node.getOwnScope().importScope(dag.scopeL);
            dag.scopeL.importScope(node.getOwnScope());
        }

        return dag;
    }

    private static DagHandle buildDag(BnfAlternation bnf) {
        DagHandle dag1 = buildDag(bnf.getElementSyntax());
        DagHandle dag2 = buildDag(bnf.getSeparatorSyntax());
        Point.chain(dag1, dag2);
        return dag1;
    }

    private static DagHandle buildDag(BnfAtom bnf) {
        if (bnf instanceof BnfUnion) {
            return buildDag((BnfUnion)bnf);
        }
        if (bnf instanceof BnfConcat) {
            return buildDag((BnfConcat)bnf);
        }
        if (bnf instanceof BnfItem) {
            return buildDag((BnfItem)bnf);
        }
        if (bnf instanceof BnfAlternation) {
            return buildDag((BnfAlternation)bnf);
        }
        return new DagHandle(new Atom(bnf));
    }

}
