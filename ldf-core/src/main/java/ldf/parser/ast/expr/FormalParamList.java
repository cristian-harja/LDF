package ldf.parser.ast.expr;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.*;

import static java.util.Collections.*;
import static ldf.parser.Util.ListBuilder;

/**
 * A list of formal parameters. Has features which can help detect errors
 * (like duplicate parameter names).
 *
 * @author Cristian Harja
 */
@Immutable
public final class FormalParamList {

    @Nonnull
    private List<FormalParam> paramsList;

    @Nonnull
    private Map<String, FormalParam> paramsMap;

    @Nonnull
    private Map<String, List<FormalParam>> duplicates;

    private FormalParamList() {}

    @Nonnull
    public List<FormalParam> getParameterList() {
        return paramsList;
    }

    @Nonnull
    public Map<String, FormalParam> getParameterMap() {
        return paramsMap;
    }

    @Nonnull
    public Map<String, List<FormalParam>> getDuplicates() {
        return duplicates;
    }

    public static class Builder extends ListBuilder<FormalParam, Builder> {

        public FormalParamList build() {
            assertNotBuilt();

            List<FormalParam> list = buildList();
            Map<String, FormalParam> map;
            Map<String, List<FormalParam>> duplicates;

            map = new LinkedHashMap<String, FormalParam>();
            duplicates = new LinkedHashMap<String, List<FormalParam>>();

            for (FormalParam p: list) {
                String id = p.getIdentifier();
                if (duplicates.containsKey(id)) {
                    duplicates.get(id).add(p);
                } else if (map.containsKey(id)) {
                    List<FormalParam> lst = new ArrayList<FormalParam>();
                    lst.add(map.remove(id));
                    lst.add(p);
                    duplicates.put(id, lst);
                } else {
                    map.put(id, p);
                }
            }

            FormalParamList fpl = new FormalParamList();

            fpl.paramsList = unmodifiableList(list);
            fpl.paramsMap = unmodifiableMap(map);

            if (duplicates.size() == 0) {
                fpl.duplicates = emptyMap();
            } else {
                for (Map.Entry<String, List<FormalParam>>
                        p: duplicates.entrySet()
                        ) {
                    duplicates.put(p.getKey(), unmodifiableList(p.getValue()));
                }
            }

            fpl.duplicates = duplicates.size() != 0 ? duplicates
                    : Collections.<String, List<FormalParam>>emptyMap();

            return fpl;
        }
    }
}
