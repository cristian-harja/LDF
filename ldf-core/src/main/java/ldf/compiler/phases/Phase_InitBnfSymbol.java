package ldf.compiler.phases;

import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.bnf.BnfSyntax;
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

        Iterator<BnfSyntax> it = file.findAllOfType(BnfSyntax.class);

        while (it.hasNext()) {
            BnfSyntax syntax = it.next();
            syntax.getAgsRoot().initSymbols();
        }

    }


}
