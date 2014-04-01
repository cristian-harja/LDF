package ldf.parser.ast;

import ldf.java_cup.runtime.Symbol;
import ldf.parser.Util;
import ldf.parser.inspect.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

import static java.util.Collections.synchronizedMap;

/**
 * TODO
 *
 * @author Cristian Harja
 */
@ThreadSafe
@SuppressWarnings("unused")
public abstract class AstNode implements Iterable<AstNode> {

    private Symbol stNode;
    private AstNode astParent;
    private AstNode astChildL;
    private AstNode astChildR;
    private AstNode astSiblingL;
    private AstNode astSiblingR;

    private final Map<Object, Object> extraInfo = synchronizedMap(
            new TreeMap<Object, Object>(Util.NATIVE_HASH_COMPARATOR));

    public final Object getExtra(Object key) {
        return extraInfo.get(key);
    }

    public final Object putExtra(Object key, Object value) {
        return extraInfo.put(key, value);
    }

    public final Map<Object, Object> getExtraInfo() {
        return extraInfo;
    }

    @Nullable
    public final Symbol getStNode() {
        return stNode;
    }

    public final synchronized void setStNode(@Nonnull Symbol stNode) {
        if (this.stNode != null) {
            throw new IllegalStateException(
                    "setStNode() must only be called ONCE, upon " +
                    "object initialization"
            );
        }
        this.stNode = stNode;
    }

    @Nullable
    public final AstNode getAstParent() {
        return astParent;
    }

    @Nullable
    public final AstNode getAstSiblingL() {
        return astSiblingL;
    }

    @Nullable
    public final AstNode getAstSiblingR() {
        return astSiblingR;
    }

    @Nullable
    public final AstNode getAstChildL() {
        return astChildL;
    }

    protected final <T extends AstNode> void addAstChildren(
            @Nullable T... children
    ) {
        if (children == null) return;
        addAstChildren(Arrays.asList(children));
    }

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

    public final Iterator<AstNode> findAllByDFS(
            final @Nullable Predicate<AstNode> P
    ) {
        return new Iterator<AstNode>() {

            boolean initialized, consumed;
            AstNode next = AstNode.this;

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
                    next = next.astParent;
                    if (next.astSiblingR != null) {
                        next = next.astSiblingR;
                        return;
                    }
                } while (next != null);
            }

            private boolean notMatching() {
                return P != null && (next == null || !P.eval(next));
            }

            private void findFirst() {
                while (notMatching()) {
                    moveToNext();
                }
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
}
