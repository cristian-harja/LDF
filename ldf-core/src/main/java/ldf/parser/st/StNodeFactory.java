package ldf.parser.st;

import ldf.java_cup.runtime.Symbol;

/**
 * @author Cristian Harja
 */
public class StNodeFactory extends LdfTokenFactory {

    private StNode lastToken;

    @Override
    protected Symbol newSymbol(
            String symName, int symCode, int parse_state
    ) {
        return new StNode(symName, symCode, parse_state);
    }

    @Override
    public Symbol newEmptySymbol(
            String name, int id, Symbol prevSymbol
    ) {
        StNode prevLeaf = ((StNode) prevSymbol).getLeafR();
        StNode s = (StNode) super.newEmptySymbol(name, id, prevLeaf);
        StNode.insertEmptySymbol(prevLeaf, s);
        return s;
    }

    @Override
    public Symbol newToken(
            String symName, int symCode,
            int lineL, int columnL, int offsetL
    ) {
        StNode tok = (StNode) super.newToken(
                symName, symCode,
                lineL, columnL, offsetL
        );
        if (lastToken != null) {
            StNode.linkTokens(lastToken, tok);
        }
        lastToken = tok;

        return tok;
    }

    @Override
    public Symbol newEOF(
            String symName, int symCode,
            int lineL, int columnL, int offsetL
    ) {
        Symbol eof = super.newEOF(
                symName, symCode,
                lineL, columnL, offsetL
        );
        lastToken = null;
        return eof;
    }
}
