package ldf.parser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

import static java.lang.System.identityHashCode;
import static java.util.Collections.*;

/**
 * @author Cristian Harja
 */
public final class Util {
    private Util() {
    }

    /**
     * Provided for convenience, as shorthand for throwing an exception
     * when {@code built} is {@code true}.
     *
     * @throws java.lang.IllegalStateException
     */
    public static void assertNotBuilt(boolean built, Class<?> cls) {
        if (built) {
            throw new IllegalStateException(
                    "Object already built: " + cls.getName()
            );
        }
    }

    /**
     * <p>Helper class for building immutable lists; used to build
     * immutable AST nodes containing a list of descendants.
     * </p>
     * <p>Typically, complex AST nodes define a {@code Builder} inner
     * class, which in some cases is a subclass of this class. </p>
     *
     * @see #add
     * @see #buildList
     * @param <ItemT> list element type
     * @param <SelfT> type of this object (returned by the {@link #add add}
     *                method)
     */
    @NotThreadSafe
    public static abstract class ListBuilder<
            ItemT, SelfT extends ListBuilder<ItemT, SelfT>
    > {
        /**
         * <p>The class that would benefit from this builder. The purpose
         * of this field is to provide a meaningful error message in the
         * event that a call to {@link #add add} is made after a call to
         * {@link #buildList}.
         * </p>
         * <p>By default (when no value is supplied to the constructor),
         * it is assumed that this {@code ListBuilder} is sub-classed by a
         * {@code Builder} class, inside the class that's being built.
         * </p>
         * <p>By this assumption, the default value of this field is
         * {@code getClass().getEnclosingClass()}.</p>
         */
        private final Class<?> targetType;

        /**
         * Whether the list has been built. If {@link #buildList buildList}
         * has been called, this field is set to {@code true} and
         * subsequent calls to {@link #add add} will yield an
         * {@link java.lang.IllegalStateException IllegalStateException}.
         */
        private boolean built;

        /**
         * Items currently added to the list.
         */
        private List<ItemT> items = new ArrayList<ItemT>();

        protected ListBuilder() {
            targetType = getClass().getEnclosingClass();
        }

        protected ListBuilder(@Nonnull Class<?> targetType) {
            this.targetType = targetType;
        }

        /**
         * Asserts that {@link #buildList} has not been called.
         *
         * @throws java.lang.IllegalStateException
         */
        protected void assertNotBuilt() {
            Util.assertNotBuilt(built, targetType);
        }

        /**
         * Adds an item to the list being built. This must not be called
         * after {@link #buildList}.
         *
         * @throws java.lang.IllegalStateException
         */
        @Nonnull
        @SuppressWarnings("unchecked")
        public SelfT add(ItemT item) {
            assertNotBuilt();
            items.add(item);
            return (SelfT) this;
        }

        /**
         * Builds an unmodifiable list with the items that have been
         * {@link #add add}ed to this builder. After this function has
         * been called, subsequent calls to {@link #add} will cause
         * an {@link java.lang.IllegalArgumentException}.
         */
        @Nonnull
        protected synchronized List<ItemT> buildList() {
            List<ItemT> items_ = items;
            int size = items_.size();
            if (!built) {
                if (size == 0) {
                    items = emptyList();
                } else if (size == 1) {
                    items = singletonList(items_.get(0));
                } else {
                    ((ArrayList)items_).trimToSize();
                    items = unmodifiableList(items_);
                }
                built = true;
            }
            return items;
        }

        public void addAll(Collection<ItemT> items) {
            for (ItemT item: items) {
                add(item);
            }
        }
    }

    /**
     * Apparently, there's no readily available interface in JDK for
     * defining a parameter-less factory method on-the-fly.
     *
     * @param <T> type of object
     */
    public interface Factory<T> {
        T factoryMethod();
    }

    /**
     *
     */
    public static final Comparator<Object> NATIVE_HASH_COMPARATOR =
            new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return identityHashCode(o1) - identityHashCode(o2);
                }
            };

    /**
     * TODO: doc
     * @param map
     * @param key
     * @param value
     * @param <K> map key type
     * @param <V> list element type
     * @throws java.lang.NullPointerException
     */
    @SuppressWarnings("ALL")
    public static <K, V> void safeMapAdd(
            @Nonnull Map<K, Collection<V>> map,
            @Nonnull CollectionType factory,
            @Nonnull K key,
            @Nullable V value
    ) {
        Collection<V> coll;
        synchronized (map) {
            coll = map.get(key);
            if (coll == null) {
                coll = factory.newInstance();
                map.put(key, coll);
            }
            coll.add(value);
        }
    }

    @SuppressWarnings("ALL")
    public static <K, V> boolean safeMapRemove(
            @Nonnull Map<K, Collection<V>> map,
            @Nonnull K key,
            @Nonnull V value
    ) {
        Collection<V> coll;
        synchronized (map) {
            coll = map.get(key);
            if (coll == null) return false;
            return coll.remove(value);
        }
    }

    public enum CollectionType {
        LINKED_LIST(new Factory<LinkedList>() {
            public LinkedList factoryMethod() {
                return new LinkedList();
            }
        }),

        ARRAY_LIST(new Factory<ArrayList>() {
            public ArrayList factoryMethod() {
                return new ArrayList();
            }
        }),

        HASH_SET(new Factory<LinkedHashSet>() {
            public LinkedHashSet factoryMethod() {
                return new LinkedHashSet();
            }
        }),

        TREE_SET(new Factory<TreeSet>() {
            public TreeSet factoryMethod() {
                return new TreeSet();
            }
        });

        private final Factory<? extends Collection> factory;

        CollectionType(Factory<? extends Collection> factory) {
            this.factory = factory;
        }

        Collection newInstance() {
            return factory.factoryMethod();
        }

    }

}
