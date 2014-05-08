package ldf.compiler.ast.decl;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.ast.Reference;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>A {@code class} declaration (not in the sense of OOP).
 * </p>
 * <p>Right now it's aimed at representing AST nodes, can only be defined
 * within a grammar and can only contain variable declarations (fields).
 * </p>
 *
 * @author Cristian Harja
 */
public final class DeclClass extends Declaration {

    @Nonnull
    private AstIdentifier id;

    @Nullable
    private Reference superClass;

    @Nonnull
    private DeclList declList;

    public DeclClass(
            @Nonnull AstIdentifier id,
            @Nullable Reference superClass,
            @Nonnull DeclList declList
    ) {
        this.id = id;
        this.superClass = superClass;
        this.declList = declList;
        addAstChildren(superClass, declList);
    }

    @Nonnull
    public AstIdentifier getId() {
        return id;
    }

    public DeclList getDeclarations() {
        return declList;
    }

    @Override
    public boolean hasOwnScope() {
        return true;
    }

    @Override
    public AstIdentifier getDeclaredSymbolName() {
        return getId();
    }

    @Override
    public NsNodeType getDeclaredSymbolType() {
        return NsNodeType.CLASS;
    }

    @Nullable
    public Reference getSuperClass() {
        return superClass;
    }

    @Nullable
    public DeclClass getSuperClassDecl() {
        if (superClass == null) return null;
        NsNode n = superClass.getReferencedNsNode();
        if (n == null) return null;
        assert n.getType() == NsNodeType.CLASS;
        return (DeclClass) n.getAstNode();
    }

}
