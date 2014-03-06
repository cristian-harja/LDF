package ldf.parser;

import ldf.java_cup.runtime.Symbol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;
import java.util.ListIterator;

/**
 * <p>This class implements a syntax tree to be used by the LDF parser.
 * The generation of this tree is optional and can be activated for
 * debugging purposes, or for integration with an IDE like IntelliJ.
 * </p>
 * <p>The LDF parser uses the LALR(1) algorithm, which means that the
 * tree will be built "bottom-up" (starting from the leaf nodes and
 * assembling the tree towards the root node). A couple of methods in this
 * class are provided with the intent of optimizing this process.
 * </p>
 * <p>The descendants of a node are themselves nodes in a linked list
 * (the {@code siblingL} and {@code siblingR} fields serve this purpose).
 * </p>
 * <p>Nodes returned by the lexer are meant to be leaves in the syntax
 * tree. Besides the {@code siblingL/R} fields (which indicate their
 * succession as child nodes within a sub-tree), the {@code tokenL/R}
 * fields make those leaf nodes part of another linked list (the one that
 * contains all tokens returned by the lexer, including EOF).
 * </p>
 * <p>When the (CUP-generated) LDF parser performs a reduce operation,
 * it invokes a one of the {@code newSymbol} methods in the {@link
 * ldf.java_cup.runtime.SymbolFactory} interface. Those methods would
 * then call {@link Symbol#setLeftRightSymbols} on the newly created
 * symbol, to indicate the leftmost and rightmost child of that node.
 * Using this information, the overloaded {@link #setLeftRightSymbols} in
 * {@code StNode} can explore the syntax tree and reconstruct a list of
 * direct descendants for the newly created node.
 * </p>
 */
@NotThreadSafe
@SuppressWarnings("unused")
public class StNode extends Symbol implements Collection<StNode> {

    // Implementing a tree (with descendants in a linked list):

    private int numChildren; // number of descendants

    private StNode parent;   // parent node
    private StNode childL;   // first child
    private StNode childR;   // last child

    private StNode siblingL; // left sibling  (parent != null)
    private StNode siblingR; // right sibling (parent != null)

    // A linked list through all the tokens (even if they're already part
    // of the syntax tree or not):

    private StNode tokenL;
    private StNode tokenR;

    // Some shortcuts through the syntax tree:

    private StNode root;
    private StNode leafL;
    private StNode leafR;


    // And now the methods:

    public StNode() {}

    public StNode(
            String symName,
            int symCode,
            int parse_state
    ) {
        super(symName, symCode, parse_state);
    }

    /**
     * Connects (in a linked list) two consecutive tokens returned by the
     * lexer. This information will be used to explore the syntax tree.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static void linkTokens(StNode t1, StNode t2) {
        if (!(t1.isLeafNode() && t2.isLeafNode())) {
            throw new IllegalArgumentException(
                    "Both symbols must be leaves."
            );
        }

        if (t1.tokenR != null || t2.tokenL != null) {
            throw new IllegalArgumentException(
                    "Tokens are already linked"
            );
        }

        t1.tokenR = t2;
        t2.tokenL = t1;
    }

    /**
     * Give the root of two sub-trees (they mustn't have a parent node),
     * this method joins them (and any unused roots in between) into
     * a linked list of children to this node.
     *
     * This is the bottom-up approach for building the syntax tree
     * (repeatedly grouping tokens and symbols until only one root
     * remains, covering all leaves).
     *
     * @throws NullPointerException
     * @throws ClassCastException
     */
    @Override
    public void setLeftRightSymbols(
            @Nonnull Symbol l,
            @Nonnull Symbol r
    ) {
        StNode ll = (StNode) l;
        StNode rr = (StNode) r;

        if (parent != null) {
            throw new IllegalStateException(
                    "This node is not acceptable as root node"
            );
        }

        if (ll.parent != null || rr.parent != null) {
            throw new IllegalArgumentException();
        }

        leafL = ll.leafL != null ? ll.leafL : ll;
        leafR = rr.leafR != null ? rr.leafR : rr;

        childL = ll;
        childR = rr;
        numChildren = 1;

        StNode next, cursor;

        cursor = ll;
        ll.parent = this;

        while (cursor != rr) {
            next = cursor.getNextRoot();
            if (next == null) {
                throw new RuntimeException("Internal error.");
            }
            ++numChildren;
            next.parent = this;
            cursor.siblingR = next;
            next.siblingL = cursor;
            cursor = next;
        }

        leafL.root = this;
        leafR.root = this;

        super.setLeftRightSymbols(l, r);
    }

    /**
     * Detaches this node from its parent (if any).
     *
     * @return whether this node had a parent
     */
    public boolean orphan() {
        if (parent == null) {
            return false;
        }
        StNode prev = siblingL;
        StNode next = siblingR;
        if (prev == null) {
            parent.childL = next;
            if (next != null) {
                next.siblingL = null;
            }
        }
        if (next == null) {
            parent.childR = prev;
            if (prev != null) {
                prev.siblingR = null;
            }
        }
        if (parent.leafL == this) {
            parent.leafL = next;
        }
        if (parent.leafR == this) {
            parent.leafR = prev;
        }
        --parent.numChildren;
        parent = null;
        root = null;

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!contains(o)) return false;
        ((StNode)o).orphan(); // assert this
        return true;
    }

    public boolean isLeafNode() {
        return childL == null && childR == null;
    }

    public boolean hasParent() {
        return parent != null;
    }

    @Nullable
    public StNode getParent() {
        return parent;
    }

    /**
     * @throws NullPointerException if {@code node} is {@code null}
     */
    public boolean isDescendantOf(StNode node) {
        StNode cursor = this;
        while (cursor != null) {
            cursor = cursor.parent;
            if (cursor == node) {
                return true;
            }
        }
        return false;
    }

    /**
     * If this node has a parent, returns the first sibling (i.e. the
     * first child of the parent node); {@code null} otherwise.
     */
    @Nullable
    public StNode getLeftmostSibling() {
        return parent != null ? parent.childL : null;
    }

    /**
     * If this node has a parent, returns the last sibling (i.e. the
     * last child of the parent node); {@code null} otherwise.
     */
    @Nullable
    public StNode getRightmostSibling() {
        return parent != null ? parent.childR : null;
    }

    @Nullable
    public StNode getLeafL() {
        return leafL;
    }

    @Nullable
    public StNode getLeafR() {
        return leafR;
    }

    @Nullable
    public StNode getChildL() {
        return childL;
    }

    @Nullable
    public StNode getChildR() {
        return childR;
    }

    @Nullable
    public StNode getSiblingL() {
        return siblingL;
    }

    @Nullable
    public StNode getSiblingR() {
        return siblingR;
    }

    @Nullable
    private StNode getPrevRoot() {
        StNode node = leafL != null ? leafL : this;
        if (node.tokenL == null) return null; // EOF
        node = node.tokenL;
        if (node.root == null) return node;

        return node.root;
    }

    @Nullable
    private StNode getNextRoot() {
        StNode node = leafR != null ? leafR : this;
        if (node.tokenR == null) return null; // EOF
        node = node.tokenR;
        if (node.root == null) return node;

        return node.root;
    }

    /**
     * Returns the number of descendants of this node.
     */
    public int size() {
        return numChildren;
    }

    /**
     * Whether this node has any descendants.
     */
    public boolean isEmpty() {
        return numChildren == 0;
    }

    /**
     * Whether this node is the parent of the given {@code StNode} object.
     *
     * @throws NullPointerException
     * @throws ClassCastException
     */
    public boolean contains(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!(o instanceof StNode)) {
            throw new ClassCastException();
        }
        return  ((StNode) o).parent ==  this;
    }

    @Nonnull
    public ListIterator<StNode> iterator() {
        return new StIterator(childL);
    }

    @Nonnull
    public ListIterator<StNode> iteratorReverse() {
        return new StIteratorRev(childR);
    }


    @SuppressWarnings("unchecked")
    private <T> T[] toArray_(@Nonnull T[] a) {
        StNode node = childL;

        for (int i = 0, n = a.length; i < n; ++i) {
            a[i] = (T) node;
            if (node != null) {
                node = node.siblingR;
            }
        }

        return a;
    }

    public boolean containsAll(@Nonnull Collection<?> c) {
        for (Object o: c) {
            if (!c.contains(o)) {
                return false;
            }
        }
        return true;
    }

    public boolean addAll(@Nonnull Collection<? extends StNode> c) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(@Nonnull Collection<?> c) {
        boolean modified = false;
        for (Object o: c) {
            if (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        boolean modified = false;
        for (Object o: c) {
            if (o instanceof StNode &&
                    ((StNode) o).parent != this) {
                ((StNode) o).orphan();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        StNode node = childL;
        while (node != null) {
            node.parent = null;
            node = node.siblingR;
        }
        childL = null;
        childR = null;
        numChildren = 0;
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return toArray_(new StNode[numChildren]);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(@Nonnull T[] a) {

        // check component type
        Class<?> componentType = a.getClass().getComponentType();
        if (componentType.isAssignableFrom(StNode.class)) {
            throw new ArrayStoreException(
                    "Can't store " +
                            StNode.class.getSimpleName() +
                            " objects into the provided array of " +
                            componentType.getCanonicalName()
            );
        }

        return toArray_(a);
    }

    @Override
    public boolean add(StNode node) {
        throw new UnsupportedOperationException();
    }

    private class StIterator implements ListIterator<StNode> {

        int index, prevIndex = - 1;
        StNode parent;
        StNode prev, next;

        public StIterator(StNode start) {
            next = start;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public StNode next() {
            prev = next;
            next = next.siblingR;
            prevIndex = index++;
            return prev;
        }

        @Override
        public boolean hasPrevious() {
            return prev != null;
        }

        @Override
        public StNode previous() {
            next = prev;
            prev = prev.siblingL;
            prevIndex = index--;
            return next;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return prevIndex;
        }

        @Override
        public void remove() {
            next.orphan();
        }

        @Override
        public void set(StNode node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(StNode node) {
            throw new UnsupportedOperationException();
        }
    }

    private class StIteratorRev implements ListIterator<StNode> {

        int index, prevIndex = -1;
        StNode parent;
        StNode prev, next;

        public StIteratorRev(StNode node) {
            next = node;
        }

        @Override
        public boolean hasPrevious() {
            return next != null;
        }

        @Override
        public StNode previous() {
            prev = next;
            next = next.siblingR;
            prevIndex = index++;
            return prev;
        }

        @Override
        public boolean hasNext() {
            return prev != null;
        }

        @Override
        public StNode next() {
            next = prev;
            prev = prev.siblingL;
            prevIndex = index--;
            return next;
        }

        @Override
        public int previousIndex() {
            return index;
        }

        @Override
        public int nextIndex() {
            return prevIndex;
        }

        @Override
        public void remove() {
            next.orphan();
        }

        @Override
        public void set(StNode node) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(StNode node) {
            throw new UnsupportedOperationException();
        }
    }

    private static void assertHasParent(StNode node) {
        if (node.parent == null) {
            throw new RuntimeException(
                    "Target node does not have a parent node"
            );
        }
    }

    private static void throwCyclicParentOf() {
        throw new IllegalArgumentException(
                "Trying to insert a node into one of its descendants " +
                        "(or itself)."
        );
    }
}
