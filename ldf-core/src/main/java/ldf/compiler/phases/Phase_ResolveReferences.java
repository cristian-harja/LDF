package ldf.compiler.phases;

import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.bnf.BnfSyntax;
import ldf.compiler.ast.decl.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

import static ldf.compiler.ast.decl.DeclWhereClause.ActionEntry;

/**
 * Resolves {@link ldf.compiler.ast.Reference}s to {@link
 * ldf.compiler.semantics.symbols.NsNode}s.
 *
 * @author Cristian Harja
 */
public final class Phase_ResolveReferences {

    private Phase_ResolveReferences() {
    }

    public static void resolveReferences(
            @Nonnull AstSourceFile file
    ) {
        resolve(file.getDeclarations());
    }

    private static void resolve(@Nonnull DeclList declList) {
        for (Declaration decl : declList.getItems()) {
            if (decl instanceof DeclGrammar) {
                resolve((DeclGrammar) decl);
            } else if (decl instanceof DeclNonTerminal) {
                resolve((DeclNonTerminal) decl);
            } else if (decl instanceof DeclClass) {
                resolve((DeclClass) decl);
            } else if (decl instanceof DeclVariable) {
                resolve((DeclVariable) decl);
            } else {
            //*
                decl.getParserContext().reportWarn(
                        decl.getParserContext().getFileName(), decl,
                        "TODO: Reference resolution for " +
                                decl.getClass().getSimpleName()
                );
            //*/
            }
        }
    }

    private static void resolve(@Nonnull DeclVariable decl) {
        // nothing to do here, apparently
    }

    private static void resolve(@Nonnull DeclGrammar decl) {
        resolve(decl.getDeclarations());
    }

    private static void resolve(@Nonnull DeclClass decl) {
        // todo: class inheritance; resolve extended class
        resolve(decl.getDeclarations());
    }

    private static void resolve(@Nonnull DeclNonTerminal decl) {
        resolve(decl.getWhereClause());
        resolve(decl.getSyntax());
    }

    private static void resolve(@Nullable DeclWhereClause whereClause) {
        if (whereClause == null) return;
        for (ActionEntry e : whereClause.getActionList()) {
            e.getScope();
        }
    }

    private static void resolve(@Nullable BnfSyntax syntax) {
        if (syntax == null) return;
        Iterator<Reference> it =  syntax.findAllOfType(Reference.class);
        DeclGrammar g = syntax.getParentOfType(DeclGrammar.class);
        while (it.hasNext()) {
            it.next().resolveAsNterm(g);
        }
    }
}
