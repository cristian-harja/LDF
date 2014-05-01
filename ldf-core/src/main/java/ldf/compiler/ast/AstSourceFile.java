package ldf.compiler.ast;

import ldf.compiler.ast.decl.DeclList;

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
}
