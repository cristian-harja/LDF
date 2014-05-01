package ldf.compiler.ast.type;

import ldf.compiler.util.Util;
import ldf.compiler.ast.AstIdentifier;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Cristian Harja
 */
public final class ObjectTypeExpr extends TypeExpression {

    @Nonnull
    private List<Entry> entries;

    private ObjectTypeExpr(@Nonnull List<Entry> entries) {
        this.entries = entries;
        for (Entry e : entries) {
            addAstChildren(e.getId(), e.getType());
        }
    }

    @Nonnull
    public List<Entry> getEntries() {
        return entries;
    }

    public static final class Entry {
        @Nonnull
        private AstIdentifier id;

        @Nonnull
        private TypeExpression type;

        public Entry(
                @Nonnull AstIdentifier id,
                @Nonnull TypeExpression type
        ) {
            this.id = id;
            this.type = type;
        }

        @Nonnull
        public AstIdentifier getId() {
            return id;
        }

        @Nonnull
        public TypeExpression getType() {
            return type;
        }
    }

    public static final class Builder
            extends Util.ListBuilder<Entry, Builder> {

        @Nonnull
        public Builder add(AstIdentifier id, TypeExpression type) {
            return add(new Entry(id, type));
        }

        public ObjectTypeExpr build() {
            return new ObjectTypeExpr(buildList());
        }

    }

}
