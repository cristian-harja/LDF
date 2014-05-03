package ldf.compiler.ast;

import ldf.compiler.ast.decl.DeclList;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Root of the AST; represents a source file.
 *
 * @author Cristian Harja
 */
@ThreadSafe
public final class AstSourceFile extends AstNode {

    @Nullable
    private Reference packageName;

    @Nonnull
    private ImportList importList;

    @Nonnull
    private DeclList declarations;

    @Nonnull
    private NsNode packageNsNode;

    public AstSourceFile(
            @Nullable Reference packageName,
            @Nonnull ImportList importList,
            @Nonnull DeclList declarations
    ) {
        this.packageName = packageName;
        this.importList = importList;
        this.declarations = declarations;
        addAstChildren(packageName, declarations);
    }

    @Nullable
    public Reference getPackageName() {
        return packageName;
    }

    @Nonnull
    public ImportList getImportList() {
        return importList;
    }

    @Nonnull
    public DeclList getDeclarations() {
        return declarations;
    }

    @Override
    public boolean hasOwnScope() {
        return true;
    }

    /**
     * <p>INTERNAL; Sets the {@link ldf.compiler.semantics.symbols.NsNode}
     * object referenced by this AST node.
     * </p>
     * <p>Even though this method is public, it is only intended for use
     * by the compiler (one of the compiler phases in {@code
     * ldf.compiler.phases.*}).
     * The first call to this method succeeds; subsequent ones will throw
     * a {@link java.lang.IllegalStateException}.</p>
     *
     * @throws java.lang.IllegalStateException
     */
    public void setPackageNsNode(@Nonnull NsNode packageNsNode) {
        Util.assertSetOnce(this.packageNsNode, "setPackageNsNode");
        this.packageNsNode = packageNsNode;
    }

    @Nonnull
    public NsNode getPackageNsNode() {
        return packageNsNode;
    }
}
