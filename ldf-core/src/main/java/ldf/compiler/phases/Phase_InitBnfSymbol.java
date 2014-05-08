package ldf.compiler.phases;

import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.decl.DeclNonTerminal;
import ldf.compiler.context.CompilerContext;

import java.util.Iterator;

/**
 * @author Cristian Harja
 */
public final class Phase_InitBnfSymbol {

    private Phase_InitBnfSymbol() {
    }

    @SuppressWarnings("unused")
    public static void initBnfSymbol(
            CompilerContext ctx,
            AstSourceFile file
    ) {

        Iterator<DeclNonTerminal> it;
        it = file.findAllOfType(DeclNonTerminal.class);

        while (it.hasNext()) {
            DeclNonTerminal nterm = it.next();
            nterm.initSymbols(ctx);
        }

    }


}
