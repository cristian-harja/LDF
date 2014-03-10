package ldf.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Cristian Harja
 */
public final class BnfJoin implements BnfObject {
    private final List<BnfMeet> items = new ArrayList<BnfMeet>();

    private final List<BnfMeet> readOnly =
            Collections.unmodifiableList(items);

    public void add(BnfMeet obj) {
        items.add(obj);
    }

    public List<BnfMeet> getItems() {
        return readOnly;
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }
}
