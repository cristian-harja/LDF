package ldf.parser.ags;

import com.google.common.collect.Iterators;
import ldf.parser.ast.bnf.BnfSyntax;
import ldf.parser.ast.decl.DeclNonTerminal;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>A special case of a {@link AgsNode.Type#UNION}.
 * </p>
 * <p>Everything inside an <b>{@link DeclNonTerminal nterm}</b> declaration
 * is modeled by a network of {@link AgsNode}s, but multiple declarations
 * can correspond to a single non-terminal.
 * </p>
 * <p>Please note that {@link AgsNode} is designed to work on nested nodes
 * only (each having at most one parent node, such as the ones in {@link
 * BnfSyntax}), but {@link AgsNode} won't work when trying to merge some
 * {@code nterm} declarations into a single one. While such an operation
 * could be carried out by creating a {@link AgsNode.Type#UNION UNION} node,
 * its descendants would have to be part of a linked list, which can cause
 * the code to break.
 * </p>
 * <p>Scenario in which the code would break: {@code grammar A} defines
 * non-terminal {@code x}, then grammars {@code B} and {@code C} extend
 * {@code A}, each adding a second {@code nterm} declaration for {@code x}.
 * </p>
 * <p>When trying to generate a parser for {@code B}, the parser generator
 * will see {@code x} as a union containing {@code A.x} and {@code B.x}.
 * If the union were an instance of {@link AgsNode}, then {@code A.x} and
 * {@code B.x} would be part of a linked list ({@code A.x} having the the
 * {@code next} field point to {@code B.x}).
 * </p>
 * <p>The problem occurs when trying to generate a parser for {@code C},
 * because an {@link AgsNode} would try to build a linked list with
 * {@code A.x} and {@code C.x}, but the {@code next} field of {@code A.x}
 * already points to {@code B.x}, so altering it would break the program
 * when trying to iterate again over B's version of {@code x}.
 * </p>
 * <p>For this reason, this class has been added to behave just like a
 * {@link AgsNode.Type#UNION}, but is backed by a {@link java.util.List}
 * for storing its children.
 * </p>
 *
 * @author Cristian Harja
 */
public final class AgsNodeUnion extends AgsNode {
    private List<AgsNode> items = new ArrayList<AgsNode>();

    public AgsNodeUnion(List<BnfSyntax> syntax) {
        super(Type.UNION);
        for (BnfSyntax s : syntax) {
            // DeclNonTerminal#syntax is @Nullable, because
            // an nterm declaration can have a missing body
            if (s == null) continue;
            items.add(s.getAstRoot().getAbstractGrammarSpec());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Iterator<AgsNode> iterator() {
        final Iterator<AgsNode> it = items.iterator();
        if (!it.hasNext()) {
            return it;
        }
        return new Iterator<AgsNode>() {
            Iterator<AgsNode> sub = it.next().iterateAs(Type.UNION);
            AgsNode next;
            boolean consumed = true;

            private void prepareNext() {
                if (!consumed) return;
                while (!sub.hasNext()) {
                    if (!it.hasNext()) {
                        next = null;
                        return;
                    }
                    sub = it.next().iterateAs(Type.UNION);
                }
                next = sub.next();
                consumed = false;
            }

            @Override
            public boolean hasNext() {
                prepareNext();
                return next != null;
            }

            @Override
            public AgsNode next() {
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

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Iterator<AgsNode> iterateAs(Type expectedType) {
        if (expectedType == Type.UNION) {
            return iterator();
        }
        return Iterators.<AgsNode>singletonIterator(this);
    }
}
