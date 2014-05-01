package ldf.compiler.phases;

import ldf.compiler.LdfParser;
import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.AstNode;
import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.decl.*;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Cristian Harja
 */
public final class Phase_CollectDeclarations {
    private Phase_CollectDeclarations() {}

    private static NsNode getPackage(
            @Nonnull NsNode root,
            @Nullable Reference ref
    ) {
        if (ref == null) {
            return root;
        }
        for (AstIdentifier id : ref.getPath()) {
            root = root.newChild(id, NsNodeType.PACKAGE, id);
        }
        return root;
    }

    public static void collectSymbols(
            LdfParser parser,
            NsNode nsNode
    ) {
        AstSourceFile f = parser.getAbstractSyntaxTree();
        nsNode = getPackage(nsNode, f.getPackageName());
        for (Declaration d : f.getDeclarations().getItems()) {
            collectSymbols(nsNode, d);
        }
    }

    private static void collectSymbols(
            NsNode nsNode,
            AstNode astNode
    ) {
        if (astNode instanceof DeclGrammar) {
            collectSymbols(nsNode, (DeclGrammar) astNode);
        } else if (astNode instanceof DeclNonTerminal) {
            collectSymbols(nsNode, (DeclNonTerminal) astNode);
        } else if (astNode instanceof DeclClass) {
            collectSymbols(nsNode, (DeclClass) astNode);
        } else if (astNode instanceof DeclVariable) {
            collectSymbols(nsNode, (DeclVariable) astNode);
        } else if (astNode instanceof DeclFunction) {
            collectSymbols(nsNode, (DeclFunction) astNode);
        } else {
            throw new RuntimeException(
                    "Can't collect symbol declaration from " +
                            astNode.getClass().getCanonicalName()
            );
        }
    }

    private static void collectSymbols(
            NsNode nsNode,
            DeclGrammar grammar
    ) {
        NsNode grammarNS = nsNode.newChild(
                grammar.getId(), NsNodeType.GRAMMAR, grammar
        );
        for (Declaration d : grammar.getDeclarations().getItems()) {
            collectSymbols(grammarNS, d);
        }
    }

    private static void collectSymbols(
            NsNode nsNode,
            DeclNonTerminal nterm
    ) {
        nsNode.newChild(nterm.getId(), NsNodeType.NTERM, nterm);
    }

    private static void collectSymbols(
            NsNode nsNode,
            DeclClass declClass
    ) {
        NsNode classNS = nsNode.newChild(
                declClass.getId(), NsNodeType.CLASS, declClass
        );
        for (Declaration d : declClass.getDeclarations().getItems()) {
            collectSymbols(classNS, d);
        }
    }

    private static void collectSymbols(
            NsNode nsNode,
            DeclVariable declVar
    ) {
        nsNode.newChild(declVar.getId(), NsNodeType.FIELD, declVar);
    }

    private static void collectSymbols(
            NsNode nsNode,
            DeclFunction declFunc
    ) {
        nsNode.newChild(declFunc.getId(), NsNodeType.METHOD, declFunc);
    }

}
