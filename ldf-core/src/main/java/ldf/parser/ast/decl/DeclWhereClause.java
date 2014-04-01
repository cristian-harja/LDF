package ldf.parser.ast.decl;

import ldf.parser.ast.AstIdentifier;
import ldf.parser.ast.AstNode;
import ldf.parser.ast.bnf.BnfAbstractAction;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static ldf.parser.Util.assertNotBuilt;

/**
 * Implements the {@code where} clause of an {@code nterm} declaration.
 * Not a declaration by itself. Backed by the {@code decl_nterm_where_}.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclWhereClause extends AstNode {

    @Nonnull
    private List<Entry> actionList;

    @Nonnull
    private Map<String, Entry> actionMap;

    @Nonnull
    private Map<String, List<Entry>> duplicates;

    private DeclWhereClause(
            @Nonnull List<Entry> actionList,
            @Nonnull Map<String, Entry> actionMap,
            @Nonnull Map<String, List<Entry>> duplicates
    ) {
        this.actionList = actionList;
        this.actionMap = actionMap;
        this.duplicates = duplicates;
        addAstChildren(actionList);
    }

    @Nonnull
    public List<Entry> getActionList() {
        return actionList;
    }

    @Nonnull
    public Map<String, Entry> getActionMap() {
        return actionMap;
    }

    @Nonnull
    public Map<String, List<Entry>> getDuplicates() {
        return duplicates;
    }

    /**
     * An entry in the {@code where} clause. Example usage: {@code {@label}
     * = {: ... :} }.
     */
    @Immutable
    public static class Entry extends AstNode {
        @Nonnull
        private AstIdentifier identifier;

        @Nonnull
        private BnfAbstractAction action;

        public Entry(
                @Nonnull AstIdentifier identifier,
                @Nonnull BnfAbstractAction action
        ) {
            this.identifier = identifier;
            this.action = action;
            addAstChildren(action);
        }

        @Nonnull
        public AstIdentifier getIdentifier() {
            return identifier;
        }

        @Nonnull
        public BnfAbstractAction getAction() {
            return action;
        }
    }

    /**
     * Builds {@link DeclWhereClause} objects.
     */
    @NotThreadSafe
    public static class Builder {
        private boolean built;
        private List<Entry> actionList;
        private Map<String, List<Entry>> actionMap;

        public Builder() {
            actionList = new ArrayList<Entry>();
            actionMap = new TreeMap<String, List<Entry>>();
        }

        public Builder add(AstIdentifier id, BnfAbstractAction action) {
            List<Entry> entries;

            assertNotBuilt(built, DeclWhereClause.class);

            if ((entries = actionMap.get(id.getName())) == null) {
                entries = new ArrayList<Entry>();
                actionMap.put(id.getName(), entries);
            }

            Entry e = new Entry(id, action);
            entries.add(e);
            actionList.add(e);

            return this;
        }

        public DeclWhereClause build() {
            Map<String, Entry> actionMap;
            Map<String, List<Entry>> duplicates;

            assertNotBuilt(built, DeclWhereClause.class);

            actionMap  = new TreeMap<String, Entry>();
            duplicates = new TreeMap<String, List<Entry>>();

            for (Map.Entry<String, List<Entry>> e: this.actionMap.entrySet()) {
                String id = e.getKey();
                List<Entry> a = e.getValue();
                if (a.size() <= 1) {
                    actionMap.put(id, a.get(0));
                } else {
                    duplicates.put(id, a);
                }
            }
            DeclWhereClause whereClause = new DeclWhereClause(
                    unmodifiableList(actionList),
                    unmodifiableMap(actionMap),
                    unmodifiableMap(duplicates)
            );
            built = true;
            return whereClause;
        }
    }
}
