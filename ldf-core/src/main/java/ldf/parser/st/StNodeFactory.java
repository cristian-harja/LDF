package ldf.parser.st;

import ldf.java_cup.runtime.Symbol;

/**
 * @author Cristian Harja
 */
public class StNodeFactory extends LdfTokenFactory {

    private StNode lastToken;

    @Override
    public Symbol startSymbol(String symName, int symCode, int parse_state) {
        StNode s = (StNode) super.startSymbol(
                symName, symCode, parse_state
        );

        /*
            It may be the case that CUP called `scanner.next_token()`
            (which in turn called `StNodeFactory.newToken()`) *before*
            `startSymbol()`.

            As a result, the `START` and first input token will not be
            linked as leaves in the syntax tree, which causes a bug in
            `StNode.insertEmptySymbol()` (called by `newEmptySymbol()`)
            when the LR(1) algorithm begins with a *reduce* action.

            The following call to `linkTokens` fixes this issue.
        */
        s.setRightPos(lastToken);
        StNode.linkTokens(s, lastToken);
        return s;
    }

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
