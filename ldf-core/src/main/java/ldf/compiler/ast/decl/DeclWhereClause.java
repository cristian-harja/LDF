package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.bnf.BnfAbstractAction;
import ldf.compiler.ast.type.TypeExpression;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static ldf.compiler.util.Util.assertNotBuilt;

/**
 * Implements the {@code where} clause of an {@code nterm} declaration.
 * Not a declaration by itself. Backed by the {@code decl_nterm_where_}.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class DeclWhereClause extends AstNode {

    @Nonnull
    private List<Entry> entryList;

    @Nonnull
    private List<ActionEntry> actionList;

    @Nonnull
    private List<LabelTypeEntry> labelList;

    private DeclWhereClause(
            @Nonnull List<Entry> entryList,
            @Nonnull List<ActionEntry> actionList,
            @Nonnull List<LabelTypeEntry> labelList
    ) {
        this.entryList = entryList;
        this.actionList = actionList;
        this.labelList = labelList;
        addAstChildren(entryList);
    }

    @Nonnull
    public List<ActionEntry> getActionList() {
        return actionList;
    }

    @Nonnull
    public List<Entry> getEntryList() {
        return entryList;
    }

    @Nonnull
    public List<LabelTypeEntry> getLabelList() {
        return labelList;
    }

    public static class Entry extends AstNode {
        private Entry() {}
    }

    /**
     * An entry in the {@code where} clause.
     * Example usage: {@code @label = {: ... :} }.
     */
    public static class ActionEntry extends Entry {
        @Nonnull
        private AstIdentifier id;

        @Nonnull
        private BnfAbstractAction action;

        public ActionEntry(
                @Nonnull AstIdentifier identifier,
                @Nonnull BnfAbstractAction action
        ) {
            this.id = identifier;
            this.action = action;
            addAstChildren(action);
        }

        @Nonnull
        public AstIdentifier getId() {
            return id;
        }

        @Nonnull
        public BnfAbstractAction getAction() {
            return action;
        }
    }

    public static class LabelTypeEntry extends Entry {
        @Nonnull
        private Reference label;

        @Nonnull
        private TypeExpression type;


        public LabelTypeEntry(
                @Nonnull Reference label,
                @Nonnull TypeExpression type
        ) {
            this.label = label;
            this.type = type;
        }

        @Nonnull
        public Reference getLabel() {
            return label;
        }

        @Nonnull
        public TypeExpression getType() {
            return type;
        }
    }

    /**
     * Builds {@link DeclWhereClause} objects.
     */
    @NotThreadSafe
    public static class Builder {
        private boolean built;

        private List<Entry> entryList;
        private List<LabelTypeEntry> labelList;
        private List<ActionEntry> actionList;

        public Builder() {
            entryList = new ArrayList<Entry>();
            labelList = new ArrayList<LabelTypeEntry>();
            actionList = new ArrayList<ActionEntry>();
        }

        public Builder add(Reference ref, TypeExpression type) {

            assertNotBuilt(built, DeclWhereClause.class);

            LabelTypeEntry e = new LabelTypeEntry(ref, type);
            entryList.add(e);
            labelList.add(e);

            return this;

        }

        public Builder add(AstIdentifier id, BnfAbstractAction action) {

            assertNotBuilt(built, DeclWhereClause.class);

            ActionEntry e = new ActionEntry(id, action);
            entryList.add(e);
            actionList.add(e);

            return this;
        }

        public DeclWhereClause build() {

            assertNotBuilt(built, DeclWhereClause.class);

            DeclWhereClause whereClause = new DeclWhereClause(
                    unmodifiableList(entryList),
                    unmodifiableList(actionList),
                    unmodifiableList(labelList)
            );
            built = true;
            return whereClause;
        }
    }
}
