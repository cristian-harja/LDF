package ldf.parser.st;

import ldf.java_cup.runtime.Symbol;
import ldf.java_cup.runtime.TokenFactoryImpl;
import ldf.parser.ast.AstNode;

/**
 * @author Cristian Harja
 */
public class TokenFactory extends TokenFactoryImpl {

    private StNode lastToken;

    @Override
    protected Symbol newSymbol(
            String symName, int symCode, int parse_state
    ) {
        return new StNode(symName, symCode, parse_state);
    }

    private Symbol addAstBackReference(Symbol sym) {
        Object value = sym.value;
        if (value instanceof AstNode) {
            AstNode astNode = ((AstNode) value);
            if (astNode.getStNode() == null) {
                astNode.setStNode(sym);
            }
        }
        return sym;
    }

    @Override
    public Symbol newSymbol(
            String name, int id,
            Symbol left, Symbol right,
            Object value
    ) {
        return addAstBackReference(super.newSymbol(
                name, id, left, right, value
        ));
    }

    @Override
    public Symbol newSymbol(String name, int id, Object value) {
        return addAstBackReference(super.newSymbol(
                name, id, value
        ));
    }

    @Override
    public Symbol newEmptySymbol(
            String name, int id,
            Symbol previousSymbol, Object value
    ) {
        return addAstBackReference(super.newEmptySymbol(
                name, id, previousSymbol, value
        ));
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
