package ldf.parser.decl;

import ldf.parser.ast.AstNode;
import ldf.parser.ast.bnf.BnfAbstractAction;
import ldf.parser.ast.bnf.BnfPlaceholder;
import ldf.parser.ast.bnf.BnfSyntax;
import ldf.parser.ast.decl.DeclNonTerminal;
import ldf.parser.ast.decl.DeclWhereClause;
import ldf.parser.inspect.Inspection;
import ldf.parser.util.Predicate;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Cristian Harja
 */
public class Inspect_BnfItem_Placeholder2
        extends Inspection<Object, DeclNonTerminal> {
    @Override
    protected boolean inspect(Object ctx, @Nonnull DeclNonTerminal obj) {

        // TODO: look for resolved placeholders
        DeclWhereClause where = obj.getWhereClause();
        if (where == null) {
            return false;
        }

        Map<String, DeclWhereClause.Entry> map = where.getActionMap();

/*
        Map<String, Collection<SymbolDef>> defs =
                obj.getNearestScope().getSymbolDefs();

        List<SymbolDef> placeholders = new ArrayList<SymbolDef>();

        for (Collection<SymbolDef> defList: defs.values()) {
            for (SymbolDef sym : defList) {
                if (sym.getType() == SymbolType.PLACEHOLDER) {
                    placeholders.add(sym);
                }
            }
        }
*/
        // TODO: import scopes from where clause
        BnfSyntax syntax = obj.getSyntax();
        if (syntax == null) {
            return false;
        }
        Iterator<AstNode> it = syntax.findAllByDFS(
                new Predicate<AstNode>() {
           @Override
           public boolean eval(AstNode target) {
               return target instanceof BnfPlaceholder;
           }
        });

        while (it.hasNext()) {
            BnfPlaceholder placeholder;
            DeclWhereClause.Entry entry;

            placeholder = (BnfPlaceholder) it.next();
            entry = map.get(placeholder.getId().getName());
            if (entry == null)  {
                continue;
            }
            BnfAbstractAction action = entry.getAction();
            AstNode actionNode = (AstNode) action;
            if (actionNode.hasOwnScope()) {
                Scope scopePlaceholder = placeholder.getOwnScope();
                Scope scopeAction = actionNode.getOwnScope();
                scopeAction.importScope(scopePlaceholder);
                scopePlaceholder.importScope(scopeAction);
            }
        }

        return true;
    }

    private Inspect_BnfItem_Placeholder2() {
        super(DeclNonTerminal.class);
    }

    private static final Inspect_BnfItem_Placeholder2
            instance = new Inspect_BnfItem_Placeholder2();

    public static Inspect_BnfItem_Placeholder2 getInstance() {
        return instance;
    }
}
