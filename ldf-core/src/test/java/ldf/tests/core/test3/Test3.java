package ldf.tests.core.test3;

import ldf.java_cup.runtime.Scanner;
import ldf.compiler.syntax.tree.StNode;
import ldf.compiler.syntax.tree.StNodeFactory;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * @author Cristian Harja
 */
public class Test3 {

    static class MyTokenFactory extends StNodeFactory {
        @Override
        protected StNode newSymbol(
                String symName, int symCode, int parse_state
        ) {
            return new StNode(symName, symCode, parse_state) {
                @Override
                public String toString() {
                    if (isLeafNode()) {
                        return value != null ? (String) value : "";
                    } else {
                        StringBuilder sb = new StringBuilder();

                        for (StNode node: this) {
                            sb.append(node.toString());
                        }

                        return sb.toString();
                    }
                }
            };
        }
    }

    private parser newParser(String resource) {
        StNodeFactory tf = new MyTokenFactory();
        InputStream in = getClass().getResourceAsStream(resource);
        InputStreamReader isr = new InputStreamReader(in);
        Scanner scanner = new Lexer(isr, tf);

        return new parser(scanner, tf);
    }

    @Test
    public void testParse() throws Exception {

        StNode s = (StNode) newParser("input.txt").parse();

        assertEquals("(1,2,(3),())", s.toString());

        Thread.sleep(200);
    }
}
