package ldf.compiler.targets.java.types;

import org.junit.Test;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import static ldf.compiler.targets.java.types.JavaClassType.getLeastUpperBounds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Cristian Harja
 */
public class LeastUpperBoundTest {
    @Test
    public void test1() throws Exception {
        Collection<Class<?>> lub;
        lub = getLeastUpperBounds(AbstractMap.class, SortedMap.class);

        assertEquals(1, lub.size());
        assertTrue(lub.contains(Map.class));
        Thread.sleep(200);
    }

    @Test
    public void test2() throws Exception {
        Collection<Class<?>> lub;
        lub = getLeastUpperBounds(Integer.class);

        assertEquals(1, lub.size());
        assertTrue(lub.contains(Integer.class));
        Thread.sleep(200);
    }

    @Test
    public void test3() throws Exception {
        Collection<Class<?>> lub;
        lub = getLeastUpperBounds(Comparable.class, Integer.class);

        assertEquals(1, lub.size());
        assertTrue(lub.contains(Comparable.class));
        Thread.sleep(200);
    }


}
