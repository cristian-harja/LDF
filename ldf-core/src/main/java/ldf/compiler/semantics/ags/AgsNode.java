package ldf.compiler.semantics.ags;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.bnf.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.*;

/**
 * <p>A (rather intricate) class which models the BNF syntax in a more
 * abstract fashion. The unnecessary complexity caused by the AST and extra
 * parentheses is removed, leaving a network of:
 * <ol>
 * <li>unions         (see <b>{@link AgsNode.Type#UNION  UNION}</b>)</li>
 * <li>concatenations (see <b>{@link AgsNode.Type#CONCAT CONCAT}</b>)</li>
 * <li>items          (see <b>{@link AgsNode.Type#ITEM   ITEM}</b>)</li>
 * </ol>
 * (as denoted by the {@link Type} enum and {@link #getType()} method).
 * </p>
 * <p><strong>Implementation details:</strong></p>
 * <p>Each node can be part of a linked list (using the {@code next} /
 * {@code prev} fields) and/or contain a list of descendants (using {@code
 * first} / {@code last}).
 * </p>
 * <p>One trick this class employs in order to reduce performance costs is
 * joining linked lists of the same kind (concatenations or unions) without
 * cloning them, but merely connecting one's end to another's.
 * </p>
 * <p>For example, if {@code list_1} has (linked) items {@code A-B-C} and
 * {@code list_2} has {@code D-E-F}, then these lists are joined by creating
 * a link between {@code C} and {@code D}. The newly created list will have
 * its {@code first} and {@code last} fields point to {@code A} and {@code
 * F} respectively.
 * </p>
 * <p>An iterator going through {@code list_1} will not run past its end,
 * because it stops at the item pointed to by {@code list_1.last}, not just
 * when the {@code next} field is {@code null}. As a result, all three list
 * objects will be unaware of each other and can be iterated independently.
 * </p>
 * <p>There is no risk of trying to link a list's end twice as long as the
 * BNF portion of the AST has a tree-like structure. In order for this
 * to happen, the parser would have to behave erratically (and have a node
 * be a direct descendant of two different nodes).
 * </p>
 *
 * @author Cristian Harja
 * @see #iterator  iterator
 * @see #iterateAs iterateAs
 */
@SuppressWarnings("unused")
public class AgsNode implements Iterable<AgsNode> {

    // for representing the syntax
    @Nonnull
    private Type type;

    @Nullable
    private AgsNode next, prev, first, last;

    // optional, additional information from the AST
    @Nullable
    private BnfAtom atom;

    // label(s) attached to this node
    private List<AstIdentifier> labels;
    private List<AstIdentifier> readOnlyLabels = emptyList();

    // quantifier(s) attached to this node
    private List<BnfQuantifier> quantifiers;
    private List<BnfQuantifier> readOnlyQuantifiers = emptyList();

    // list of labels visible to this node (used when `atom` is an action)
    Snapshot snapshot;

    public static enum Type {
        ITEM,
        CONCAT,
        UNION
    }

    AgsNode(@Nonnull Type t) {
        type = t;
    }

    private AgsNode(@Nullable BnfAtom atom) {
        type = Type.ITEM;
        this.atom = atom;
    }

    private AgsNode(
            @Nonnull Type type,
            @Nullable AgsNode first,
            @Nullable AgsNode last
    ) {
        this.type = type;
        this.first = first;
        this.last = last;
    }

    public boolean hasLabels() {
        return !readOnlyLabels.isEmpty();
    }

    public boolean hasQuantifiers() {
        return !readOnlyQuantifiers.isEmpty();
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    @Nullable
    public BnfAtom getAtom() {
        return atom;
    }

    @Nonnull
    public List<AstIdentifier> labels() {
        return readOnlyLabels;
    }

    @Nonnull
    public List<BnfQuantifier> quantifiers() {
        return readOnlyQuantifiers;
    }

    public void initSymbols() {
        new AgsSymTable().initSymbols(this);
    }


    /**
     * List this node's children (if any).
     */
    @Nonnull
    public Iterator<AgsNode> iterator() {
        if (type == Type.ITEM) {
            return emptyIterator();
        }
        return iterateAs(type);
    }

    /**
     * <p>Treats the node as if it's of the specified type (either {@link
     * AgsNode.Type#UNION UNION} or {@link AgsNode.Type#CONCAT CONCAT}) and
     * lists the items contained within.
     * </p>
     * <p>Consider the top-level expression in: {@code nterm A ::= a b c;}
     * which is expected to be a {@link AgsNode.Type#UNION UNION}, but
     * because it would only contain one concatenation (namely, {@code a b
     * c}), a separate node representing the union would be redundant (in
     * the AST, it already is).
     * </p>
     * <p>For this reason, the private {@link #agsInit(java.util.List,
     * AgsNode.Type) agsInit(List, Type)} method (which creates union and
     * concatenation nodes) will skip the creation of singleton nodes and
     * return only the contained node.
     * </p>
     * <p>This method is provided to help the user iterate through nodes
     * which are supposed to be unions, but physically are concatenations
     * (or vice-versa). If the node is of the correct type, this method will
     * iterate through its descendants normally (just like {@link #iterator
     * iterator}), but if not, it will return a singleton iterator for the
     * node itself.</p>
     * <p>This has the effect that, for example, if the user is expecting
     * to see a union containing one concatenation (which in turn contains
     * a few items), there will be (physically) only one node representing
     * the concatenation, but this method will allow the user to see the
     * logical structure of the grammar specification (a union containing
     * only the concatenation), because the iterator simulates a normal
     * iteration over a singleton union.
     * </p>
     *
     * @param expectedType the expected (logical) type of this node
     */
    @Nonnull
    public Iterator<AgsNode> iterateAs(Type expectedType) {
        if (expectedType == Type.ITEM) {
            throw new IllegalArgumentException(
                    "Argument can only be Type.UNION or Type.CONCAT"
            );
        }
        if (type != expectedType) {
            return Iterators.singletonIterator(this);
        }
        return new UnmodifiableIterator<AgsNode>() {

            private AgsNode next = first;

            @Override
            public boolean hasNext() {
                return next != null && next.prev != last;
            }

            @Override
            public AgsNode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                AgsNode ret = next;
                next = ret.next;
                return ret;
            }

        };
    }

    // debugging
    @Override
    public String toString() {
        return type.toString();
    }

    /**
     * @return AGS node associated with a {@link ldf.compiler.ast.bnf.BnfItem}.
     */
    @Nonnull
    public static AgsNode agsInit(@Nonnull BnfItem item) {
        AgsNode node = item.getAtom().getAbstractGrammarSpec();
        if (item.getLabel() != null && !node.quantifiers().isEmpty()) {
            node = new AgsNode(Type.ITEM, node, node);
        }
        node.addLabel(item.getLabel());
        node.addQuantifier(item.getQuantifier());
        return node;
    }

    /**
     * @return AGS node associated with a {@link ldf.compiler.ast.bnf.BnfAtom}.
     */
    @Nonnull
    public static AgsNode agsInit(@Nonnull BnfAtom atom) {
        if (atom instanceof BnfItem) {
            return agsInit((BnfItem) atom);
        }
        if (atom instanceof BnfUnion) {
            return agsInit((BnfUnion) atom);
        }
        if (atom instanceof BnfConcat) {
            return agsInit((BnfConcat) atom);
        }
        return new AgsNode(atom);
    }

    /**
     * @return AGS node associated with a {@link ldf.compiler.ast.bnf.BnfUnion}.
     */
    @Nonnull
    public static AgsNode agsInit(@Nonnull BnfUnion item) {
        return agsInit(item.getItems(), Type.UNION);
    }

    /**
     * @return AGS node associated with a {@link ldf.compiler.ast.bnf.BnfConcat}.
     */
    @Nonnull
    public static AgsNode agsInit(@Nonnull BnfConcat item) {
        return agsInit(item.getItems(), Type.CONCAT);
    }

    /**
     * Builds unions and concatenations (which receive special treatment)
     */
    @Nonnull
    private static AgsNode agsInit(
            @Nonnull List<? extends BnfAtom> items,
            @Nonnull Type type
    ) {
        AgsNode first = null, last = null, next, prev = null;
        AgsNode emptyBranch = null;
        boolean atomic = false;

        for (BnfAtom node : items) {
            AgsNode ags = node.getAbstractGrammarSpec();

            // If an item has labels or quantifiers, it must be treated
            // as a whole, instead of trying to break it down into parts.
            atomic = (
                    ags.type == Type.ITEM ||
                            !ags.labels().isEmpty() ||
                            !ags.quantifiers().isEmpty()
            );

            // If it's not `atomic`, the node might be optimized away.
            // Are we looking at an empty concatenation or union ?
            if (!atomic && ags.isEmpty()) {

                // Is this also the only node in the current list?
                if (items.size() == 1) {
                    return ags; // reuse the empty node
                    /*
                        If we didn't do this, the for loop would exit with
                        first = last = null, which will cause a new, empty
                        `AgsNode` to be created (see the last line).
                    */
                }

                /*
                    If we're looking at an empty branch within a union,
                    we'll save it for later (instead of adding it to the
                    list of children of the node being created).

                    If multiple such branches are found, only the first
                    one will be added to the node. The others will be
                    discarded right here.
                */
                if (type == Type.UNION && emptyBranch == null) {
                    emptyBranch = ags;
                }
                continue;
            }

            atomic = atomic || ags.type != type;

            /*
                At this point, if `atomic` is false, it means that the list
                of children can be continued by concatenating it with the
                list of children of `ags` (see the  Implementation details"
                section from the documentation of this class).

                The `first()` and `last()` methods will fetch the ends of
                the list of children of `ags`.
            */

            // Add the item to the list of descendants of the node being
            // created...
            if (prev == null) {
                // ... by starting the list with it
                first = atomic ? ags : ags.first(type);
            } else {
                // ... by continuing the linked list of the previous child
                next = atomic ? ags : ags.first(type);
                prev.next = next;
                next.prev = prev;
            }
            prev = atomic ? ags : ags.last(type);
        }

        // Append the `empty` branch (same thing as above).
        if (emptyBranch != null) {
            if (prev == null) {
                first = emptyBranch;
            } else {
                next = emptyBranch;
                prev.next = next;
                next.prev = prev;
            }
            prev = emptyBranch;
        }

        // Save a reference to the end of the list.
        if (prev != null) {
            last = atomic ? prev : prev.last(type);
        }

        // If the resulting list only has one item, return it.
        if (first == last && first != null) {
            return first;
        }

        // Create a new node (having 0 or >1 children; just not exactly 1).
        return new AgsNode(type, first, last);
    }

    /**
     * Whether this node has no descendants.
     */
    public boolean isEmpty() {
        return first == null;
    }

    @Nonnull
    private AgsNode first(Type type) {
        return (type != this.type || first == null)
                ? this : first.first(type); // tail call
    }

    @Nonnull
    private AgsNode last(Type type) {
        return (type != this.type || last == null)
                ? this : last.last(type); // tail call
    }

    /*
        The following couple of methods are optimized to create as few
        objects as possible in order to hold a list of labels and/or
        quantifiers.
    */

    private void addLabel(@Nullable BnfLabel label) {
        if (label == null) return;
        synchronized (this) {
            int size = readOnlyLabels.size();
            if (size < 2) {
                if (size == 0) {
                    readOnlyLabels = singletonList(label.getId());
                    return;
                } else if (size == 1) {
                    labels = new ArrayList<AstIdentifier>();
                    labels.add(readOnlyLabels.get(0));
                    readOnlyLabels = unmodifiableList(labels);
                }
            }
            labels.add(label.getId());
        }
    }

    private void addQuantifier(@Nullable BnfQuantifier q) {
        if (q == null) return;
        synchronized (this) {
            int size = readOnlyQuantifiers.size();
            if (size < 2) {
                if (size == 0) {
                    readOnlyQuantifiers = singletonList(q);
                    return;
                } else if (size == 1) {
                    quantifiers = new ArrayList<BnfQuantifier>();
                    quantifiers.add(readOnlyQuantifiers.get(0));
                    readOnlyQuantifiers = unmodifiableList(quantifiers);
                }
            }
            quantifiers.add(q);
        }
    }

}
