package ldf.compiler.semantics.types;

import ldf.compiler.ast.decl.DeclClass;
import ldf.compiler.ast.decl.DeclVariable;
import ldf.compiler.ast.type.TypeExpression;
import ldf.compiler.semantics.symbols.NsNode;
import ldf.compiler.semantics.symbols.NsNodeType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * @author Cristian Harja
 */
public class LdfClassType extends DataType {
    private NsNode nsNode;
    private LdfClassType parent;
    private Set<LdfClassType> closure;

    public LdfClassType(NsNode nsNode) {
        init(
                closure = new HashSet<LdfClassType>(),
                new HashSet<NsNode>(),
                nsNode
        );
    }

    private LdfClassType(
            Set<LdfClassType> closure,
            Set<NsNode> seen,
            NsNode node
    ) {
        init(closure, seen, node);
    }

    private void init(
            Set<LdfClassType> closure,
            Set<NsNode> seen,
            NsNode node
    ) {
        assert node.getType() == NsNodeType.CLASS;
        this.nsNode = node;
        closure.add(this);
        DeclClass c = (DeclClass) nsNode.getAstNode();
        DeclClass p = c.getSuperClassDecl();
        if (p == null) return;
        NsNode n = p.getDeclaredNsNode();
        if (n == null || seen.contains(n)) return;
        seen.add(n);
        parent = new LdfClassType(closure, seen, n);
        closure.add(parent);
    }

    @Override
    protected boolean isAssignableFromImpl(DataType t) {
        if (t instanceof LdfClassType) {
            LdfClassType c = (LdfClassType) t;
            return c.closure.contains(this);
        } else if (t instanceof ObjectType) {
            ObjectType o = (ObjectType) t;
            for (NsNode field : nsNode.getChildren(NsNodeType.VARIABLE)) {
                DeclVariable var = (DeclVariable) field.getAstNode();
                TypeExpression expr = var.getType();
                if (expr == null) return false;
                DataType dt = expr.getDataType();
                if (dt == null) return false;
                DataType objField = o.fields.get(field.getName());
                if (objField == null) continue;
                if (!dt.isAssignableFrom(objField)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void format(@Nonnull Appendable out) throws IOException {
        nsNode.format(out);
    }

    @Override
    protected boolean equals(@Nonnull DataType t) {
        return this == t || t instanceof LdfClassType && equals(
                (LdfClassType) t
        );
    }

    private boolean equals(@Nonnull LdfClassType t) {
        return nsNode == t.nsNode;
    }

    public LdfClassType getSuperClass() {
        return parent;
    }

    public Stack<LdfClassType> getParentList() {
        Stack<LdfClassType> s = new Stack<LdfClassType>();
        LdfClassType c = this;
        do {
            s.push(c);
            c = c.parent;
        } while (c != null);
        return s;
    }

    @Override
    public DataType getLeastUpperBound(DataType t) {
        if (!(t instanceof LdfClassType)) return NoType.INSTANCE;
        Stack<LdfClassType> s1, s2;
        s1 = this.getParentList();
        s2 = ((LdfClassType) t).getParentList();
        DataType commonAncestor = NoType.INSTANCE;

        while (!s1.isEmpty() && !s2.isEmpty() &&
                s1.peek().equals(s2.peek())
        ) {
            commonAncestor = s1.peek();
            s1.pop(); s2.pop();
        }

        return commonAncestor;
    }
}
