package com.adnant1.coordinator.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashRingTest {

    private ConsistentHashRing ring;

    /**
     * Helper to set private fields via reflection.
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = ConsistentHashRing.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Helper to invoke the private hash(String) method.
     */
    private int hashViaReflection(ConsistentHashRing target, String key) throws Exception {
        Method hashMethod = ConsistentHashRing.class.getDeclaredMethod("hash", String.class);
        hashMethod.setAccessible(true);
        return (int) hashMethod.invoke(target, key);
    }

    /**
     * Initialize router with two nodes and small v-node count for basic tests.
     */
    @BeforeEach
    void setUp() throws Exception {
        ring = new ConsistentHashRing();
        setField(ring, "nodeUrls", List.of("http://node1", "http://node2"));
        setField(ring, "VIRTUAL_NODES", 10);
        Method init = ConsistentHashRing.class.getDeclaredMethod("init");
        init.invoke(ring);
    }

    @Test
    void singleNodeAlwaysWins() throws Exception {
        ConsistentHashRing ring = new ConsistentHashRing();
        setField(ring, "nodeUrls", List.of("http://only-node"));
        setField(ring, "VIRTUAL_NODES", 5);
        Method init = ConsistentHashRing.class.getDeclaredMethod("init");
        init.invoke(ring);

        for (int i = 0; i < 10; i++) {
            assertEquals("http://only-node", ring.getNodesForId("user-" + i));
        }
    }

    // @Test
    // void deterministicMapping() {
    //     String id = "user-123";
    //     String first = ring.getNodeForId(id);
    //     String second = ring.getNodeForId(id);
    //     assertEquals(first, second, "Mapping should be deterministic for same ID");
    // }

    @Test
    void nullEmptyValidation() {
        assertThrows(IllegalArgumentException.class, () -> ring.getNodesForId(null));
        assertThrows(IllegalArgumentException.class, () -> ring.getNodesForId(""));
    }

    @Test
    void emptyRingGuard() throws Exception {
        ConsistentHashRing emptyRing = new ConsistentHashRing();
        // Do not configure nodeUrls → init should fail
        Method init = ConsistentHashRing.class.getDeclaredMethod("init");
        assertThrows(Exception.class, () -> init.invoke(emptyRing));
    }

    @Test
    void wrapAroundBehavior() throws Exception {
        // Two nodes, one virtual node each
        ConsistentHashRing wrap = new ConsistentHashRing();
        setField(wrap, "nodeUrls", List.of("A", "B"));
        setField(wrap, "VIRTUAL_NODES", 1);
        Method init = ConsistentHashRing.class.getDeclaredMethod("init");
        init.invoke(wrap);

        // Access the ring to find max key
        Field ringField = ConsistentHashRing.class.getDeclaredField("ring");
        ringField.setAccessible(true);
        @SuppressWarnings("unchecked")
        TreeMap<Integer, String> ringMap = (TreeMap<Integer, String>) ringField.get(wrap);
        int maxKey = ringMap.lastKey();

        // Find an ID whose hash > maxKey
        String candidate = null;
        for (int i = 0; i < 10000; i++) {
            String id = "id-" + i;
            if (hashViaReflection(wrap, id) > maxKey) {
                candidate = id;
                break;
            }
        }
        assertNotNull(candidate, "Should find an ID that wraps around");
        String expected = ringMap.firstEntry().getValue();
        assertEquals(expected, wrap.getNodesForId(candidate), "IDs past max key should wrap to first node");
    }
}
