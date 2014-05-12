package ldf.compiler.util;

/**
 * @author Cristian Harja
 */
class FunctionBuilder {
    final String name;
    StringBuilder sb = new StringBuilder();
    int indent = 4;
    boolean built;

    FunctionBuilder(String name) {
        this.name = name;
        putIndent(sb);
        beginParseDecl(name);
    }

    String makeReference() {
        return "parse_" + name;
    }

    void add(CharSequence cs) {
        sb.append(cs);
    }

    void add(char cs) {
        sb.append(cs);
    }

    void putIndent(StringBuilder sb) {
        for (int i = 0, n = indent; i < n; ++i) {
            sb.append(' ');
        }
    }

    void newLine(int indentChange) {
        add('\n');
        indent += indentChange;
        putIndent(sb);
    }

    void newLine() {
        StringBuilder sb = this.sb;
        sb.append('\n');
        putIndent(sb);
    }

    String build() {
        endParseDecl();
        built = true;
        return sb.toString();
    }

    public boolean isBuilt() {
        return built;
    }

    void beginParseDecl(String name) {
        //  ParseResult parse_???(int index) {
        //      //  memoization
        //      ParseResult result = get(index, ???);
        //      if (result != null) return result;
        //
        add("ParseResult parse_");
        add(name);
        add("(int index) {");
        newLine(+4);

        add("// memoization");
        newLine();
        add("ParseResult result = get(index, \"");
        add(name);
        add("\");");
        newLine();
        add("if (result != null) return result;");
        newLine();
        newLine();
        add("// parse");
        newLine();

    }

    void declareValues() {
        //      Map<String, ?> values;
        //      value = new HashMap<String, ?>();
        //
        add("Map<String, ?> values;");
        newLine();
        add("value = new HashMap<String, ?>();");
        newLine();
        newLine();
    }

    private void endParseDecl() {
        //  }
        newLine(-4);
        sb.append("}");
    }

}
