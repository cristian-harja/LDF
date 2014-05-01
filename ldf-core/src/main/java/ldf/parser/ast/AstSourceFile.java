package ldf.parser.ast;

import ldf.parser.ast.decl.DeclList;

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
    private DeclList declarations;

    public AstSourceFile(
            @Nullable Reference packageName,
            @Nonnull DeclList declarations
    ) {
        this.packageName = packageName;
        this.declarations = declarations;
        addAstChildren(packageName, declarations);
    }

    @Nullable
    public Reference getPackageName() {
        return packageName;
    }

    @Nonnull
    public DeclList getDeclarations() {
        return declarations;
    }

}
