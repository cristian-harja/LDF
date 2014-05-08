package ldf.compiler.semantics.ags;

import ldf.compiler.ast.AstIdentifier;
import ldf.compiler.context.ParserContext;
import ldf.compiler.semantics.types.DataType;
import ldf.compiler.semantics.types.NoType;
import ldf.compiler.semantics.types.TypeEnv;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents symbols (introduced by labels in the BNF syntax) which are
 * visible to grammar actions.
 *
 * @author Cristian Harja
 */
@SuppressWarnings("unused")
public final class AgsSymbol {

    AgsNode agsNode;
    AstIdentifier label;

    AgsSymbol originalSymbol;
    AgsSymbol iteratedSymbol;
    List<AgsSymbol> unionSymbols;
    List<AgsSymbol> unionSymbolsReadOnly = Collections.emptyList();

    Map<String, AgsSymbol> nestedSymbols;
    Map<String, AgsSymbol> nestedSymbolsReadOnly = Collections.emptyMap();

    DataType deductedType;
    DataType expectedType;
    DataType actualType;

    // package-local constructor

    AgsSymbol() {}

    // getters for everything

    /**
     * Whether this symbol was derived from an iteration (thus being
     * exposed to subsequent grammar actions as an array).
     */
    public boolean isIterated() {
        return iteratedSymbol != null;
    }

    /**
     * Whether this symbol was derived from a union.
     */
    public boolean isUnion() {
        return unionSymbols != null;
    }

    /**
     * Whether this symbols hasn't suffered any transformations since its
     * creation (such as iteration or union).
     */
    public boolean isOriginal() {
        return originalSymbol == this;
    }

    /**
     * Returns the {@link AgsNode} referenced by this symbol (or {@code
     * null} if {@code isOriginal() == false}).
     */
    @Nullable
    public AgsNode getAgsNode() {
        return agsNode;
    }

    /**
     * AST node corresponding to the label which introduced this symbol.
     * If this symbol is derived from a union, this method returns only
     * the first such label.
     */
    // fixme
    @Nonnull
    public AstIdentifier getLabel() {
        return label;
    }

    /**
     * Retrieve the original symbol definition (without transformations).
     * Doesn't work if the symbol is derived from a union (must use {@link
     * #getUnionSymbols()} instead).
     */
    @Nullable
    public AgsSymbol getOriginalSymbol() {
        return originalSymbol;
    }

    /**
     * If this symbol was defined as an iteration of another one,
     * this method returns a reference to the previous definition.
     */
    @Nullable
    public AgsSymbol getIteratedSymbol() {
        return iteratedSymbol;
    }

    /**
     * It this symbol was derived from a union, this method returns the
     * list of symbols which were combined into this one. Otherwise,
     * returns the empty list.
     */
    @Nonnull
    public List<AgsSymbol> getUnionSymbols() {
        return unionSymbolsReadOnly;
    }

    /**
     * Symbols nested under the current one (or an empty map if there are
     * none).
     */
    @Nonnull
    public Map<String, AgsSymbol> getNestedSymbols() {
        return nestedSymbolsReadOnly;
    }

    /**
     * <p>If the user didn't indicate a data type for this symbol (via the
     * {@code where} clause), this method returns {@code null}.
     * </p>
     * <p>Otherwise, it returns the corresponding {@link DataType} object,
     * or {@link NoType} if the type could not be resolved (or there was
     * some other sort of type error).
     * </p>
     */
    @Nullable
    public DataType getExpectedType() {
        return expectedType;
    }

    /**
     * Returns the deducted data type for this symbol (or {@link NoType}
     * in case of a conflict). The deduction is done by the compiler, using
     * type information from sub-expressions in the BNF syntax.
     */
    @Nonnull
    public DataType getDeductedType() {
        return deductedType;
    }

    /**
     * Returns the actual type (which is either the deducted type, the
     * user-specified expected type, or {@link NoType}).
     */
    @Nonnull
    public DataType getActualType() {
        int mask = (deductedType != null) ? 1 : 0;
        mask |= (expectedType != null) ? 2 : 0;
        switch (mask) {
            case 0:
                actualType = NoType.INSTANCE;
                break;
            case 1:
                actualType = deductedType;
                break;
            case 2:
                actualType = expectedType;
                break;
            case 3:
                assert expectedType != null;
                assert deductedType != null;
                if (!expectedType.isAssignableFrom(deductedType)) {
                    ParserContext ctx = label.getParserContext();
                    ctx.reportError(label, ctx.i18n().getString(
                            "type.not_assignable"
                    ), expectedType.toString(), deductedType.toString());
                }
                actualType = expectedType;
                break;
        }
        assert actualType != null;
        return actualType;
    }

    // for debugging
    @Override
    public String toString() {
        DataType type = null;
        if (actualType != null) {
            type = actualType;
        } else if (expectedType != null) {
            type = expectedType;
        } else if (deductedType != null) {
            type = deductedType;
        }
        return label.getName() + " : " + type;
    }

    static AgsSymbol makeUnion(
            TypeEnv typeEnv,
            Collection<AgsSymbol> items
    ) {
        AgsSymbol e = new AgsSymbol();
        if (!items.isEmpty()) {
            e.label = items.iterator().next().label;
        }
        e.unionSymbols = new ArrayList<AgsSymbol>(items);
        e.unionSymbolsReadOnly = Collections.unmodifiableList(
                e.unionSymbols
        );
        int size = e.unionSymbols.size();
        List<DataType> types = new ArrayList<DataType>(size);

        DataType dt = null;

        for (AgsSymbol symbol : e.unionSymbols) {
            types.add(symbol.getActualType());
        }
        e.deductedType = typeEnv.computeLeastUpperBound(types);
        if (e.deductedType == NoType.INSTANCE) {
            AstIdentifier label = e.label;
            if (label != null) {
                ParserContext ctx = label.getParserContext();
                ctx.reportError(label, ctx.i18n().getString(
                        "type.no_lub"
                ), label.getName());
            }
        }
        return e;
    }

    static AgsSymbol makeIteration(AgsSymbol original) {
        AgsSymbol s = new AgsSymbol();
        s.label = original.label;
        s.originalSymbol = original.originalSymbol;
        s.iteratedSymbol = original;
        s.deductedType = original.getActualType().getArrayType();
        return s;
    }

}
