package ldf.parser.ast;

/**
 * @author Cristian Harja
 */
public final class BnfSyntax {
    private BnfJoin root;

    public BnfSyntax(BnfJoin root) {
        this.root = root;
    }

    public BnfJoin getRoot() {
        return root;
    }
}
