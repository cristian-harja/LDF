package ldf.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Cristian Harja
 */
public class StNodeTest {

    @Test
    public void isDescendantOf_test1() throws Exception {
        StNode n1 = new StNode();
        StNode n2 = new StNode();
        StNode n3 = new StNode();

        n2.setLeftRightSymbols(n3, n3);
        n1.setLeftRightSymbols(n2, n2);

        assertTrue(n2.isDescendantOf(n1));
        assertTrue(n3.isDescendantOf(n1));
        assertTrue(n3.isDescendantOf(n2));

        assertFalse(n1.isDescendantOf(n1));
        assertFalse(n2.isDescendantOf(n2));
        assertFalse(n3.isDescendantOf(n3));

        assertFalse(n1.isDescendantOf(n2));
        assertFalse(n1.isDescendantOf(n3));
        assertFalse(n2.isDescendantOf(n3));

    }

    @Test
    public void setLeftRightSymbols_test1() throws Exception {
        StNode n1 = new StNode();
        StNode n2 = new StNode();
        StNode n3 = new StNode();
        StNode n4 = new StNode();

        StNode.linkTokens(n1, n2);
        StNode.linkTokens(n2, n3);

        n4.setLeftRightSymbols(n1, n3);

        assertTrue(n1.isDescendantOf(n4));
        assertTrue(n2.isDescendantOf(n4));
        assertTrue(n3.isDescendantOf(n4));
    }

    @Test
    public void setLeftRightSymbols_test2() throws Exception {
        StNode n1 = new StNode();
        StNode n11 = new StNode();
        StNode n12 = new StNode();
        StNode n13 = new StNode();
        StNode n121 = new StNode();
        StNode n122 = new StNode();
        StNode n123 = new StNode();

        StNode.linkTokens(n11, n121);
        StNode.linkTokens(n121, n122);
        StNode.linkTokens(n122, n123);
        StNode.linkTokens(n123, n13);

        n12.setLeftRightSymbols(n121, n123);
        n1.setLeftRightSymbols(n11, n13);

        assertTrue(n11.isDescendantOf(n1));
        assertTrue(n12.isDescendantOf(n1));
        assertTrue(n13.isDescendantOf(n1));
        assertTrue(n121.isDescendantOf(n12));
        assertTrue(n122.isDescendantOf(n12));
        assertTrue(n123.isDescendantOf(n12));
        assertTrue(n121.isDescendantOf(n1));
        assertTrue(n122.isDescendantOf(n1));
        assertTrue(n123.isDescendantOf(n1));
    }

    @Test
    public void contains_test1() throws Exception {
        // create nodes
        StNode n1 = new StNode();
        StNode n11 = new StNode();
        StNode n12 = new StNode();
        StNode n13 = new StNode();
        StNode n121 = new StNode();
        StNode n122 = new StNode();
        StNode n123 = new StNode();

        // link leaves
        StNode.linkTokens(n11, n121);
        StNode.linkTokens(n121, n122);
        StNode.linkTokens(n122, n123);
        StNode.linkTokens(n123, n13);

        // build tree
        n12.setLeftRightSymbols(n121, n123);
        n1.setLeftRightSymbols(n11, n13);

        // test on self
        for (StNode n: new StNode[]{
                n1, n11, n12, n13, n121, n122, n123
        }) {
            assertFalse(n.contains(n));
        }

        // test direct descendants of n1
        assertTrue (n1.contains(n11));
        assertTrue (n1.contains(n12));
        assertTrue (n1.contains(n13));

        // (and reversed)
        assertFalse(n11.contains(n1));
        assertFalse(n12.contains(n1));
        assertFalse(n13.contains(n1));

        // test indirect descendants of n1
        assertFalse(n1.contains(n121));
        assertFalse(n1.contains(n122));
        assertFalse(n1.contains(n123));

        // (and reversed)
        assertFalse(n1.contains(n121));
        assertFalse(n1.contains(n122));
        assertFalse(n1.contains(n123));

        // test direct descendants of n2
        assertTrue (n12.contains(n121));
        assertTrue (n12.contains(n122));
        assertTrue (n12.contains(n123));

        // (and reversed)
        assertFalse(n121.contains(n12));
        assertFalse(n122.contains(n12));
        assertFalse(n123.contains(n12));
    }

    @Test
    public void orphan_test1() throws Exception {
        StNode n1 = new StNode();
        StNode n2 = new StNode();
        StNode n3 = new StNode();
        n2.setLeftRightSymbols(n3, n3);
        n1.setLeftRightSymbols(n2, n2);

        assertTrue(n3.isDescendantOf(n1));

        n2.orphan();

        assertFalse(n3.isDescendantOf(n1));

    }

    @Test
    public void remove_test1() throws Exception {
        StNode n0 = new StNode();
        StNode n1 = new StNode();
        StNode n2 = new StNode();
        StNode n3 = new StNode();

        StNode.linkTokens(n1, n2);
        StNode.linkTokens(n2, n3);

        n0.setLeftRightSymbols(n1, n3);

        assertFalse(n0.remove(n0));
        assertEquals(n0.size(), 3);

        assertTrue(n0.remove(n2));
        assertEquals(n0.size(), 2);

        assertTrue(n0.remove(n1));
        assertEquals(n0.size(), 1);

        assertTrue(n0.remove(n3));
        assertEquals(n0.size(), 0);

    }
}
