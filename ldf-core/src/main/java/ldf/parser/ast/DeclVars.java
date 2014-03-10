package ldf.parser.ast;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Cristian Harja
 */
public final class DeclVars extends Declaration {
    List<DeclVariable> items = new LinkedList<DeclVariable>();

    public void add(DeclVariable var) {
        items.add(var);
    }

}
