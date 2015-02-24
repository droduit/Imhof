package ch.epfl.imhof;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;

public class AttributesTest {
    private final static String UNKNOWN_KEY = "randomkey";
    private final static String DEFAULT_STR = "default";
    private final static int DEFAULT_INT = 42;

    private Map<String, String> attrs;
    private Map<String, String> overInterAttrs;
    private Map<String, String> halfInterAttrs;
    private Map<String, String> noInterAttrs;
    private Map<String, String> emptyAttrs;

    @Before
    public void setUp () {
        Map<String, String> hs = new HashMap<String, String>();

        this.emptyAttrs = Collections.unmodifiableMap(new HashMap<String, String>(hs));

        hs.put("Hello", "World");
        hs.put("Oh", "Hay");

        this.halfInterAttrs = Collections.unmodifiableMap(new HashMap<String, String>(hs));

        hs.put("foo", "bar");
        hs.put("int", "123");
        hs.put("float", "12.3");

        this.attrs = Collections.unmodifiableMap(new HashMap<String, String>(hs));

        hs.put(UNKNOWN_KEY, DEFAULT_STR);

        this.overInterAttrs = Collections.unmodifiableMap(new HashMap<String, String>(hs));

        hs.clear();
        hs.put(UNKNOWN_KEY, DEFAULT_STR);

        this.noInterAttrs = Collections.unmodifiableMap(new HashMap<String, String>(hs));
    }

    private void assertAttributesEquals (Map<String, String> expected, Attributes toTest) {
        assertEquals(expected.keySet().size(), toTest.size());

        assertAttributesPresent(expected, toTest);
    }

    private void assertAttributesPresent (Map<String, String> expected, Attributes toTest) {
        for (String key : expected.keySet()) {
            assertEquals(expected.get(key), toTest.get(key));
        }
    }

    @Test
    public void testBuilder () {
        Attributes.Builder ab = new Attributes.Builder();

        for (String key : this.attrs.keySet()) {
            ab.put(key, this.attrs.get(key));
        }

        assertAttributesEquals(this.attrs, ab.build());
    }

    @Test
    public void testConstructor () {
        assertAttributesEquals(this.attrs, new Attributes(this.attrs));
    }

    @Test
    public void testIsEmpty () {
        Attributes emptyAttrs = new Attributes(new HashMap<String, String>());
        Attributes filledAttrs = new Attributes(this.attrs);

        assertTrue(emptyAttrs.isEmpty());
        assertFalse(filledAttrs.isEmpty());
    }

    @Test
    public void testContains () {
        Attributes as = new Attributes(this.attrs);

        for (String key : this.attrs.keySet()) {
            assertTrue(as.contains(key));
        }

        assertFalse(as.contains(UNKNOWN_KEY));
    }

    @Test
    public void testGet () {
        Attributes as = new Attributes(this.attrs);

        /* Basic get */
        for (String key : this.attrs.keySet()) {
            assertEquals(this.attrs.get(key), as.get(key));
        }

        /* Get with default string value */
        assertNull(as.get(UNKNOWN_KEY));
        assertEquals(DEFAULT_STR, as.get(UNKNOWN_KEY, DEFAULT_STR));

        for (String key : this.attrs.keySet()) {
            assertEquals(this.attrs.get(key), as.get(key, DEFAULT_STR));
        }

        /* Get with default int value */
        assertEquals(123, as.get("int", DEFAULT_INT));
        assertEquals(DEFAULT_INT, as.get("foo", DEFAULT_INT));
        assertEquals(DEFAULT_INT, as.get("float", DEFAULT_INT));
        assertEquals(DEFAULT_INT, as.get(UNKNOWN_KEY, DEFAULT_INT));
    }

    @Test
    public void testKeepOnlyKeys () {
        Attributes as = new Attributes(this.attrs);

        assertAttributesEquals(this.emptyAttrs, as.keepOnlyKeys(this.emptyAttrs.keySet()));
        assertAttributesEquals(this.halfInterAttrs, as.keepOnlyKeys(this.halfInterAttrs.keySet()));
        assertAttributesEquals(this.attrs, as.keepOnlyKeys(this.attrs.keySet()));

        Attributes oas = as.keepOnlyKeys(this.overInterAttrs.keySet());
        
        assertNull(oas.get(UNKNOWN_KEY));
        assertTrue(oas.contains(UNKNOWN_KEY));
        assertAttributesPresent(this.attrs, oas);

        Attributes eas = as.keepOnlyKeys(this.noInterAttrs.keySet());

        assertNull(eas.get(UNKNOWN_KEY));
        assertTrue(oas.contains(UNKNOWN_KEY));
        for (String key : this.attrs.keySet()) {
            assertFalse(eas.contains(key));
        }
    }
}
