package ldf.compiler.syntax.tree;

import ldf.java_cup.runtime.Symbol;
import ldf.java_cup.runtime.TokenFactoryImpl;
import ldf.compiler.ParserContext;
import ldf.compiler.ast.AstNode;

/**
 * @author Cristian Harja
 */
public class LdfTokenFactory extends TokenFactoryImpl {

    private ParserContext parserContext;

    private Symbol addAstBackReference(Symbol sym) {
        Object value = sym.value;
        if (value instanceof AstNode) {
            AstNode astNode = ((AstNode) value);
            if (astNode.getSymbol() == null) {
                astNode.setSymbol(sym);
            }
            if (astNode.getParserContext() == null) {
                astNode.setParserContext(parserContext);
            }
        }
        return sym;
    }

    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
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

}
