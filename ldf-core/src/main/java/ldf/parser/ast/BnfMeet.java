package ldf.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Cristian Harja
 */
public final class BnfMeet implements BnfObject {
    private final List<BnfObject> items = new ArrayList<BnfObject>();

    private final List<BnfObject> readOnly =
            Collections.unmodifiableList(items);


    public void add(BnfObject obj) {
        items.add(obj);
    }

    public List<BnfObject> getItems() {
        return readOnly;
    }

    @Override
    public BnfObject.Type getType() {
        return BnfObject.Type.MEET;
    }
}
