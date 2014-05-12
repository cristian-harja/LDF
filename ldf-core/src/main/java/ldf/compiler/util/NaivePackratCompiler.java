package ldf.compiler.util;

import ldf.compiler.ast.Reference;
import ldf.compiler.ast.bnf.BnfAtom;
import ldf.compiler.ast.bnf.BnfQuantifier;
import ldf.compiler.ast.expr.LiteralString;
import ldf.compiler.semantics.ags.AgsNode;
import ldf.compiler.semantics.ags.AgsNodeUnion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;

/**
 * @author Cristian Harja
 */
public final class NaivePackratCompiler extends ParserGeneratorBase {

    @Override
    protected void doCompile() throws IOException {
        compileNtermList();
    }

    Map<String, NtermCompiler> functions;

    public NaivePackratCompiler() {
        functions = new LinkedHashMap<String, NtermCompiler>();
    }

    private NtermCompiler getFunctionBuilder(
            String name, AgsNode node
    ) {
        NtermCompiler nc = functions.get(name);
        if (nc != null) return nc;
        nc = new NtermCompiler(name, node);
        functions.put(name, nc);
        return nc;
    }

    private void transferBytes(
            StringBuilder out, Reader in
    ) throws IOException {
        char[] buffer = new char[1024];
        int read;
        while ((read = in.read(buffer)) != -1){
            out.append(buffer, 0 , read);
        }
    }

    private void compileNtermList() throws IOException {

        for (Map.Entry<String, AgsNodeUnion> e :
                getNtermSyntax().entrySet()) {
            NtermCompiler nc = getFunctionBuilder(e.getKey(), e.getValue());
            if (!nc.isBuilt()) {
                nc.compile();
            }
        }

        StringBuilder sb = new StringBuilder();

        transferBytes(sb, new InputStreamReader(
                getClass().getResourceAsStream(
                        "/ldf/compiler/packrat_runtime.txt"
                )
        ));

        sb.append("public class ");
        sb.append(getGrammarName());
        sb.append(" extends PackratRuntime {\n");
        sb.append("    public ");
        sb.append(getGrammarName());
        sb.append("(String input) { super(input); }\n");


        for (NtermCompiler nterm : functions.values()) {
            sb.append('\n');
            sb.append(nterm.build());
        }

        sb.append("\n");
        sb.append("}\n");

        System.out.println(sb.toString());

    }

    private class NtermCompiler extends FunctionBuilder {

        private AgsNode node;

        NtermCompiler(String name, AgsNode node) {
            super(name);
            this.node = node;
        }

        void compile() {
            int branchNo = 0, itemNo;
            Iterator<AgsNode> union, concat;
            union = node.iterateAs(AgsNode.Type.UNION);

            add("boolean isOK = true;");newLine();
            add("int lastIndex = 0;");newLine();
            newLine();

            while (union.hasNext()) {
                AgsNode branch = union.next();
                branchNo++;

                add("//branch " + branchNo);newLine();
                add("{"); newLine(+4);

                itemNo = 0;
                concat = branch.iterateAs(AgsNode.Type.CONCAT);
                while (concat.hasNext()) {
                    AgsNode item = concat.next();
                    itemNo++;
                    boolean iteration = item.hasQuantifiers();

                    if (iteration) {
                        BnfQuantifier q = item.quantifiers().iterator().next();
                        Number n = q.getMin();
                        Number m = q.getMax();

                        add("while(true) {");newLine(+4);
                        add("int iterations = 0;");newLine(+4);

                        if (n != null) {
                            add("int min = ");
                            add(n.toString());
                            add(";"); newLine();
                        } else {
                            add("int min = 0;");
                        }

                        if (m != null) {
                            add("int max = ");
                            add(m.toString());
                            add(";"); newLine();
                        } else {
                            add("int max = Integer.MAX_VALUE;");
                            newLine();
                        }

                        NtermCompiler nc = getFunctionBuilder(
                                name + "$B" + branchNo + "I" + itemNo,
                                item
                        );

                        add("ParseResult res = ");
                        add(nc.makeReference());
                        add("(lastIndex);"); newLine();

                        add("if (res.isError()) {"); newLine(+4);
                        add("if (min > iterations) isOK = false;"); newLine();
                        add("break;");
                        newLine(-4); add("}"); newLine();
                        add("iterations++;"); newLine();
                        add("if (iterations == max) break;");

                        newLine(-4);add("}");

                    } else {

                        if (itemNo == 1) {
                            add("{"); newLine(+4);
                            add("lastIndex = index;"); newLine();
                        } else {
                            add("if (isOK) {"); newLine(+4);
                            add("lastIndex = result.nextIndex;"); newLine();
                        }

                        if (item.getType() != AgsNode.Type.ITEM) {
                            NtermCompiler nc = getFunctionBuilder(
                                    name + "$B" + branchNo + "I" + itemNo,
                                    item
                            );
                            add("result = ");
                            add(nc.makeReference());
                            add("(lastIndex);");
                            newLine();
                            addErrorCheck();
                        } else {
                            compileItem(item);
                        }

                        newLine(-4); add("}");

                    }

                    // TODO: labels?

                    newLine();
                }

                add("// epilogue"); newLine();
                add("if (isOK)  return put(index, \"");
                add(name);
                add("\", lastIndex);");
                newLine(-4); add("}");
                newLine();
            }

            newLine();
            add("return PARSE_ERROR;");
            newLine();
        }

        private void addErrorCheck() {
            add("if (isOK && result.isError()) isOK = false;");
            newLine();
        }

        private void compileItem(AgsNode node) {
            BnfAtom atom = node.getAtom();
            assert atom != null;

            switch(atom.getBnfAtomType()) {
                case REFERENCE:
                    Reference ref = (Reference) atom;
                    String id = getOnlyElement(ref.getPath()).getName();
                    AgsNodeUnion n = getNtermSyntax().get(id);
                    NtermCompiler nc = getFunctionBuilder(id, n);

                    add("result = ");
                    add(nc.makeReference());
                    add("(lastIndex)");
                    newLine();
                    addErrorCheck();
                    break;

                case LITERAL_CHAR:
                case LITERAL_STRING:
                    LiteralString str = (LiteralString) atom;
                    add("result = matchString(lastIndex, \"");
                    add(escapeStringValue(str.getFullString()));
                    add("\");");
                    newLine();
                    addErrorCheck();
                    break;

                case ITEM:
                    add("/* huh? impossibru! */");
                    newLine();
                    break;

                case ACTION:
                case GUARD:
                case PLACEHOLDER:
                    add("/* TODO (actions) */");
                    newLine();
            }

        }


    }

    private String escapeStringValue(String str) {
        str = str.replace("\n", "\\n");
        str = str.replace("\"", "\\\"");
        return str;
    }



}
