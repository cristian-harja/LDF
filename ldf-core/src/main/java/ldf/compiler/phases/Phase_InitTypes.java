package ldf.compiler.phases;

import ldf.compiler.ast.AstSourceFile;
import ldf.compiler.ast.Reference;
import ldf.compiler.ast.type.ArrayTypeExpr;
import ldf.compiler.ast.type.ClassTypeExpr;
import ldf.compiler.ast.type.ObjectTypeExpr;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.context.CompilerContext;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.types.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * Runs after {@link ldf.compiler.phases.Phase_ResolveReferences} and
 * initializes {@link ldf.compiler.semantics.types.DataType} structures
 * found within {@link ldf.compiler.ast.type.TypeExpression} nodes.
 *
 * @author Cristian Harja
 */
public class Phase_InitTypes {

    public static void initTypes(
            @Nonnull CompilerContext ctx,
            @Nonnull AstSourceFile file
    ) {
        locateClasses(ctx, file);
        initTypes(file);
    }

    private static void initTypes(AstSourceFile file) {
        Iterator<TypeExpression> it;

        it = file.findAllOfType(TypeExpression.class);
        while (it.hasNext()) {
            TypeExpression expr = it.next();
            if (expr.getDataType() != null) {
                continue;
            }
            initTypes(expr);
        }
    }

    private static void locateClasses(
            CompilerContext ctx,
            AstSourceFile file
    ) {
        TypeEnv env;
        Iterator<ClassTypeExpr> it;

        env = ctx.getTypeEnvironment();
        it = file.findAllOfType(ClassTypeExpr.class);

        while (it.hasNext()) {
            ClassTypeExpr expr = it.next();
            Reference ref = expr.getReference();

            // find the class in LDF's namespace, without reporting errors
            NsNode n = ref.tryResolveAsClass();

            if (n != null) {
                // the class exists, now do it for real
                n = ref.resolveAsClass();
                assert n != null;
                expr.setDataType(new LdfClassType(n));
                continue;
            }

            // try to find the class in the target language's namespace
            DataType dt = env.resolveClass(ref);
            if (dt != null) {
                expr.setDataType(dt);
                continue;
            }

            // class not found anywhere; try resolving it again in LDF's
            // namespace, only this time report the errors to the user
            ref.resolveAsClass();

        }

    }

    private static void initTypes(@Nullable TypeExpression type) {
        if (type == null) return;
        if (type instanceof ClassTypeExpr) {
            initTypes((ClassTypeExpr) type);
        } else if (type instanceof ObjectTypeExpr) {
            initTypes((ObjectTypeExpr) type);
        } else if (type instanceof ArrayTypeExpr)
            initTypes((ArrayTypeExpr) type);
    }

    private static void initTypes(@Nonnull ClassTypeExpr type) {
        if (type.getDataType() == null) {
            type.setDataType(NoType.INSTANCE);
        }
    }

    private static void initTypes(@Nonnull ObjectTypeExpr objectType) {
        ObjectType.Builder b = new ObjectType.Builder();
        for (ObjectTypeExpr.Entry e : objectType.getEntries()) {
            TypeExpression fieldType = e.getType();
            initTypes(fieldType);
            b.add(e.getId().getName(), fieldType.getDataType());
        }
    }

    private static void initTypes(@Nonnull ArrayTypeExpr arrayType) {
        TypeExpression baseType = arrayType.getBaseType();
        initTypes(baseType);
        arrayType.setDataType(baseType.getDataType().getArrayType());
    }


}
