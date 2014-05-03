package ldf.compiler.ast;

import ldf.compiler.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents the list of {@code import} statements at the beginning of a
 * file. Backed by the {@code import_list_} non-terminal.
 *
 * @author Cristian Harja
 */
public final class ImportList extends AstNode {

    private List<Entry> entries;

    private ImportList(List<Entry> entries) {
        this.entries = entries;
        addAstChildren(entries);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * Represents one {@code import} statement.
     */
    public static class Entry extends AstNode {

        @Nonnull
        private Reference importRef;

        @Nullable
        private AstIdentifier importAs;

        public Entry(
                @Nonnull Reference importRef,
                @Nullable AstIdentifier importAs
        ) {
            this.importRef = importRef;
            this.importAs = importAs;
            addAstChildren(importRef, importAs);
        }

        @Nonnull
        public Reference getImportRef() {
            return importRef;
        }

        @Nullable
        public AstIdentifier getImportAs() {
            return importAs;
        }
    }

    /**
     * Builds {@link ldf.compiler.ast.ImportList} objects.
     */
    public static class Builder extends Util.ListBuilder<Entry, Builder> {

        public Builder add(
                @Nonnull Reference importRef,
                @Nullable AstIdentifier importAs
        ) {
            return add(new Entry(importRef, importAs));
        }

        public ImportList build() {
            assertNotBuilt();
            return new ImportList(buildList());
        }
    }

}
