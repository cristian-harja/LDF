package ldf.parser.ast;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cristian Harja
 */
public final class DeclWhereClause {
    Map<String, DeclWhereClauseEntry> actionMap
            = new LinkedHashMap<String, DeclWhereClauseEntry>();

    List<DeclWhereClauseEntry> duplicates;

    public void add(DeclWhereClauseEntry entry) {

    }
}
