package ldf.compiler.ast;

import ldf.compiler.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Cristian Harja
 */
public class ImportList {

    private List<Entry> entries;

    private ImportList(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry {

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
