package ldf.compiler.semantics.ags;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
* @author Cristian Harja
*/
public final class Snapshot implements Cloneable {
    Map<String, AgsSymbol> precedingSymbols;
    Map<String, AgsSymbol> localSymbols;

    Snapshot(
            Map<String, AgsSymbol> preceding
    ) {
        precedingSymbols = Collections.unmodifiableMap(
                new LinkedHashMap<String, AgsSymbol>(preceding)
        );

        localSymbols = Collections.emptyMap();
    }

    Snapshot(
            Map<String, AgsSymbol> preceding,
            Map<String, AgsSymbol> local
    ) {
        precedingSymbols = Collections.unmodifiableMap(
                new LinkedHashMap<String, AgsSymbol>(preceding)
        );

        localSymbols = Collections.unmodifiableMap(
                new LinkedHashMap<String, AgsSymbol>(local)
        );
    }

    @Nonnull
    public Map<String, AgsSymbol> getPrecedingSymbols() {
        return precedingSymbols;
    }

    @Nonnull
    public Map<String, AgsSymbol> getLocalSymbols() {
        return localSymbols;
    }

}
