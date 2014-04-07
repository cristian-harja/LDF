package ldf.parser.ast;

import ldf.java_cup.runtime.Symbol;
import ldf.parser.Util;
import ldf.parser.decl.Scope;
import ldf.parser.util.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

import static java.util.Collections.synchronizedMap;

/**
 * <p>Base class for all the nodes in the Abstract Syntax Tree (AST).</p>
 *
 * <p>Allows for an immutable list of child nodes. Similarly to how {@link
 * ldf.parser.st.StNode} is implemented, the list of child nodes is not
 * stored in a {@link java.util.List}, but the {@code AstNode} objects
 * themselves are connected in a doubly-linked list.</p>
 *
 * <p>Also, each node has an associated {@link TreeMap} which can hold
 * extra information related to the node.</p>
 *
 * @see #addAstChildren(AstNode[])
 * @see #getExtra
 * @see #putExtra
 * @see #iterator
 * @see #findAllByDFS
 *
 * @author Cristian Harja
 */
@ThreadSafe
@SuppressWarnings("unused")
public abstract class AstNode implements Iterable<AstNode> {

    private Symbol  symbol;
    private AstNode astParent;
    private AstNode astChildL;
    private AstNode astChildR;
    private AstNode astSiblingL;
    private AstNode astSiblingR;

    private Scope ownScope, nearestScope;
    private Map<Object, Object> extraInfo;

    /**
     * Retrieves extra information associated with this node.
     * The key (provided as argument to this function) can be any object,
     * chosen by whichever class originated that information.
     */
    public final Object getExtra(Object key) {
        return getExtraInfo().get(key);
    }

    /**
     * Stores some extra information associated with this node.
     * The key (provided as argument to this function) can be any object,
     * chosen by whichever class originated that information.
     */
    public final Object putExtra(Object key, Object value) {
        return getExtraInfo().put(key, value);
    }

    /**
     * @return a reference to the the internal map which stores the
     *         extra information associated with this node.
     */
    public final Map<Object, Object> getExtraInfo() {
        if (extraInfo != null) {
            return extraInfo;
        }
        synchronized (this) {
            if (extraInfo != null) {
                return extraInfo;
            }
            extraInfo = synchronizedMap(new TreeMap<Object, Object>(
                    Util.NATIVE_HASH_COMPARATOR
            ));
        }
        return extraInfo;
    }

    /**
     * @return a reference to the {@link ldf.java_cup.runtime.Symbol}
     *         object whose semantic value is the current AST node. This
     *         value is set by {@link ldf.parser.st.StNodeFactory}.
     */
    public final Symbol getSymbol() {
        return symbol;
    }

    /**
     * <p>Sets the {@link ldf.java_cup.runtime.Symbol} object for this AST
     * node. Even though this method is public, it is only intended for use
     * by the parser (more specifically, by the {@link
     * ldf.parser.st.StNodeFactory} class).</p>
     *
     * <p>The first call to this method succeeds; subsequent ones will
     * throw a {@link java.lang.IllegalStateException}.</p>
     *
     * @throws java.lang.IllegalStateException
     */
    public final synchronized void setSymbol(@Nonnull Symbol stNode) {
        if (this.symbol != null) {
            throw new IllegalStateException(
                    "setSymbol() must only be called ONCE, upon " +
                    "object initialization"
            );
        }
        this.symbol = stNode;
    }

    @Nullable
    public final AstNode getAstParent() {
        return astParent;
    }

    @Nonnull
    public final AstNode assertGetAstParent() {
        AstNode parent = astParent;
        if (parent == null) {
            throw new RuntimeException("assertGetAstParent() failed");
        }
        return parent;
    }

    /**
     * Looks for a parent node with the given type.
     *
     * @param maxDistance maximum distance to go searching (a negative
     *        value means any distance). If zero, it always returns null.
     */
    @SuppressWarnings("unchecked")
    public final <T> T getParentOfType(
            @Nonnull Class<T> type,
            int maxDistance
    ) {
        AstNode cursor = this;
        while (maxDistance-- != 0) {
            cursor = cursor.astParent;
            if (cursor == null) {
                break;
            }
            if (type.isAssignableFrom(cursor.getClass())) {
                return (T) cursor;
            }
        }
        return null;
    }

    public final <T> T getParentOfType(@Nonnull Class<T> type) {
        return getParentOfType(type, -1);
    }

    public final boolean hasParentOfType(@Nonnull Class<?> type) {
        return getParentOfType(type) != null;
    }

    public final boolean hasParentOfType(
            @Nonnull Class<?> type,
            int maxDistance
    ) {
        return getParentOfType(type, maxDistance) != null;
    }

    /**
     * @return whether the node given as argument is a descendant of the
     *         current node
     */
    public final boolean hasDescendant(@Nonnull AstNode child) {
        AstNode cursor = child;
        while (cursor != null) {
            cursor = cursor.astParent;
            if (cursor == this) {
                return true;
            }
        }
        return false;
    }

    public final AstNode getAstSiblingL() {
        return astSiblingL;
    }

    public final AstNode getAstSiblingR() {
        return astSiblingR;
    }

    public final AstNode getAstChildL() {
        return astChildL;
    }

    public final AstNode getAstChildR() {
        return astChildR;
    }


    /**
     * <p>Adds a list of nodes to this node's list of descendants. Before
     * attempting this operation, all nodes are verified to prevent the
     * creation of a cyclic parent-of relationship. In doing so, the method
     * throws an {@link java.lang.IllegalArgumentException} before
     * attempting to add the first node.</p>
     *
     * <p>This method should be called from the constructor of the
     * sub-classes.</p>
     */
    protected final <T extends AstNode> void addAstChildren(
            @Nullable T... children
    ) {
        if (children == null) return;
        addAstChildren(Arrays.asList(children));
    }

    /**
     * Same as {@link #addAstChildren(AstNode[])}, but accepts a {@link
     * java.util.Collection} as its parameter.
     */
    protected final synchronized void addAstChildren(
            @Nullable Collection<? extends AstNode> children
    ) {
        if (children == null) return;
        validate(children);
        for (AstNode node: children) {
            if (node == null) continue;
            addChild(node);
        }
    }

    /**
     * @return an iterator for this node's children.
     */
    @Nonnull
    @Override
    public final Iterator<AstNode> iterator() {
        return new Iterator<AstNode>() {

            private AstNode node = astChildL;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public AstNode next() {
                AstNode n = node;
                if (node == null) {
                    throw new NoSuchElementException(
                            "No more elements"
                    );
                }
                node = node.astSiblingR;
                return n;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * @return an iterator for this node's children, in reversed order.
     */
    @Nonnull
    public final Iterator<AstNode> iteratorReverse() {
        return new Iterator<AstNode>() {

            private AstNode node = astChildR;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public AstNode next() {
                AstNode n = node;
                if (node == null) {
                    throw new NoSuchElementException(
                            "No more elements"
                    );
                }
                node = node.astSiblingL;
                return n;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public final Iterator<AstNode> findAllByDFS() {
        return findAllByDFS(null);
    }

    /**
     * @param P optional {@link ldf.parser.util.Predicate}, for filtering
     *          out nodes that are irrelevant to the calling function.
     *
     * @return an iterator that walks through the descendants of this node
     *         in a DFS fashion.
     */
    public final Iterator<AstNode> findAllByDFS(
            final @Nullable Predicate<AstNode> P
    ) {
        return new Iterator<AstNode>() {

            boolean initialized, consumed;
            AstNode initial = AstNode.this;
            AstNode next = initial;

            private void moveToNext() {
                if (next == null) return;

                if(next.astChildL != null) {
                    next = next.astChildL;
                    return;
                }

                if (next.astSiblingR != null) {
                    next = next.astSiblingR;
                    return;
                }

                do {
                    if (next == initial) {
                        next = null;
                        return;
                    }
                    if (next.astSiblingR != null) {
                        next = next.astSiblingR;
                        return;
                    }
                    next = next.astParent;
                } while (next != null);
            }

            private boolean notMatching() {
                return P != null && (next == null || !P.eval(next));
            }

            private void findFirst() {
                while (notMatching()) {
                    moveToNext();
                }
                initialized = true;
            }

            private void findNext() {
                do {
                    moveToNext();
                } while (next != null && notMatching());
            }

            private void prepareNext() {
                if (initialized) {
                    if (consumed) {
                        findNext();
                    }
                } else {
                    findFirst();
                }
                consumed = false;
            }

            @Override
            public boolean hasNext() {
                prepareNext();
                return next != null;
            }

            @Override
            public AstNode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                consumed = true;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private void addChild(@Nonnull AstNode node) {
        if (astChildR != null) {
            astChildR.astSiblingR = node;
            node.astSiblingL = astChildR;
        } else {
            astChildL = node;
        }
        astChildR = node;
        node.astParent = this;
    }

    private boolean canNotAdd(@Nonnull AstNode node) {
        if (node.astParent != null) {
            return true;
        }
        AstNode cursor = node;
        while (cursor != null) {
            cursor = cursor.astParent;
            if (cursor == node) {
                return true;
            }
        }
        return false;
    }

    private void validate(
            @Nonnull Collection<? extends AstNode> children
    ) {
        Iterator<? extends AstNode> iterator;
        int index = 0;
        for (iterator = children.iterator(); iterator.hasNext(); index++) {
            AstNode node = iterator.next();
            if (node != null && canNotAdd(node)) {
                throw new IllegalArgumentException(
                        "Unsuitable node as argument " + (index + 1)
                );
            }

        }
    }

    /**
     * If (an overridden version of) this method returns {@code true},
     * {@link #initScopes} will allocate a {@link ldf.parser.decl.Scope}
     * object for this object.
     */
    public boolean hasOwnScope() {
        return false;
    }

    public final Scope getOwnScope() {
        return ownScope; // only if hasOwnScope()
    }

    public final Scope getNearestScope() {
        return ownScope == null ? nearestScope : ownScope;
    }

    /**
     * Recursively initializes the scope information for this node and its
     * descendants. This method must not be called more than once for any
     * given node.
     */
    public synchronized void initScopes() {
        if (nearestScope != null) {
            throw new IllegalStateException(
                    "Scope previously initialized for node"
            );
        }
        if (astParent != null) {
            nearestScope = astParent.getNearestScope();
        }
        if (hasOwnScope()) {
            ownScope = new Scope(nearestScope);
        }
        for (AstNode node: this) {
            node.initScopes();
        }
    }

}
