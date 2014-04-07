package ldf.parser.ast.expr;

import ldf.parser.ast.AstNode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;
import static ldf.parser.Util.ListBuilder;

/**
 * A list of formal parameters. Has features which can help detect errors
 * (like duplicate parameter names).
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class FormalParamList extends AstNode {

    @Nonnull
    private List<FormalParam> paramsList;

    @Nonnull
    private Map<String, FormalParam> paramsMap;

    @Nonnull
    private Map<String, List<FormalParam>> duplicates;

    private FormalParamList(
            @Nonnull List<FormalParam> paramsList,
            @Nonnull Map<String, FormalParam> paramsMap,
            @Nonnull Map<String, List<FormalParam>> duplicates
    ) {
        this.paramsList = paramsList;
        this.paramsMap = paramsMap;
        this.duplicates = duplicates;
        addAstChildren(paramsList);
    }

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

    @Override
    public boolean hasOwnScope() {
        return true;
    }

    /**
     * Builds {@link FormalParamList} objects.
     */
    public static class Builder extends ListBuilder<FormalParam, Builder> {

        public FormalParamList build() {
            assertNotBuilt();

            List<FormalParam> list = buildList();
            Map<String, FormalParam> map;
            Map<String, List<FormalParam>> duplicates;

            map = new LinkedHashMap<String, FormalParam>();
            duplicates = new LinkedHashMap<String, List<FormalParam>>();

            for (FormalParam p: list) {
                String id = p.getId().getName();
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

            if (duplicates.size() == 0) {
                duplicates = emptyMap();
            } else {
                for (Map.Entry<String, List<FormalParam>>
                        p: duplicates.entrySet()
                        ) {
                    duplicates.put(p.getKey(), unmodifiableList(p.getValue()));
                }
                duplicates = unmodifiableMap(duplicates);
            }

            return new FormalParamList(
                    unmodifiableList(list),
                    unmodifiableMap(map),
                    duplicates
            );
        }
    }
}
