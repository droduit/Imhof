package ch.epfl.imhof.osm;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.HashMap;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.Attributes;

public class OSMNodeTest {
	private static final double DOUBLE_DELTA = 0.000001;
	private static final String DEFAULT_STRING_KEY = "foo";
	private static final String DEFAULT_STRING_VALUE = "bar";
	private static final String DEFAULT_INT_KEY = "int";
	private static final String DEFAULT_INT_VALUE = "1337";
	private static final String UNKNOWN_KEY = "randomkey";

	private int id;
	private PointGeo position;
	private HashMap<String, String> attrsHash;
	private Attributes attrs;
	private OSMNode node;

	private void attributesEquals (HashMap<String, String> attrs, OSMNode node) {
		attributesEquals(attrs, node.attributes());
	}

	private void attributesEquals (HashMap<String, String> attrsHash, Attributes attrs) {
		assertEquals(attrsHash.size(), attrs.size());

		for (String key : attrsHash.keySet()) {
			assertEquals(attrsHash.get(key), attrs.get(key));
		}
	}

	private OSMNode.Builder newOSMNodeBuilder () {
		OSMNode.Builder nodeBuilder = new OSMNode.Builder(this.id, this.position);

		nodeBuilder.setAttribute(DEFAULT_STRING_KEY, DEFAULT_STRING_VALUE);
		nodeBuilder.setAttribute(DEFAULT_INT_KEY, DEFAULT_INT_VALUE);

		return nodeBuilder;
	}

	@Before
	public void setUp () {
		this.id = 42;
		this.position = new PointGeo(1.2, 0.7);
		this.attrsHash = new HashMap<String, String>();

		this.attrsHash.put(DEFAULT_STRING_KEY, DEFAULT_STRING_VALUE);
		this.attrsHash.put(DEFAULT_INT_KEY, DEFAULT_INT_VALUE);

		this.attrs = new Attributes(this.attrsHash);

		this.node = new OSMNode(this.id, this.position, this.attrs);
	}

    @Test
    public void testConstructor () {
		OSMNode node = new OSMNode(this.id, this.position, this.attrs);
		assertNotNull(node);
    }

	@Test (expected = NullPointerException.class)
	public void testConstructorPositionException () {
		new OSMNode(this.id, null, this.attrs);
	}

	@Test (expected = NullPointerException.class)
	public void testConstructorAttributesException () {
		new OSMNode(this.id, this.position, null);
	}

	@Test
	public void testId () {
		assertEquals(this.node.id(), this.id);
	}

	@Test
	public void testAttributes () {
		Attributes attrs = this.node.attributes();

		assertEquals(this.attrs, attrs);
		attributesEquals(this.attrsHash, attrs);
	}

	@Test
	public void testPosition () {
		PointGeo pos = this.node.position();

		assertEquals(this.position, pos);
		assertEquals(this.position.longitude(), pos.longitude(), DOUBLE_DELTA);
		assertEquals(this.position.latitude(), pos.latitude(), DOUBLE_DELTA);
	}

	@Test
	public void testHasAttribute () {
		assertTrue(this.node.hasAttribute(DEFAULT_STRING_KEY));
		assertTrue(this.node.hasAttribute(DEFAULT_INT_KEY));
		assertFalse(this.node.hasAttribute(UNKNOWN_KEY));
	}

	@Test
	public void testAttributeValue () {
		assertEquals(this.attrs.get(DEFAULT_STRING_KEY), this.node.attributeValue(DEFAULT_STRING_KEY));
		assertEquals(this.attrs.get(DEFAULT_INT_KEY), this.node.attributeValue(DEFAULT_INT_KEY));
		assertNull(this.node.attributeValue(UNKNOWN_KEY));
	}

	@Test
	public void testOSMNodeBuilder () {
		OSMNode node = newOSMNodeBuilder().build();

		assertEquals(this.id, node.id());
		assertEquals(this.position, node.position());
		attributesEquals(this.attrsHash, node);
	}

	@Test (expected = NullPointerException.class)
	public void testOSMNodeBuilderPositionException () {
		new OSMNode.Builder(this.id, null);
	}

	@Test (expected = IllegalStateException.class)
	public void testOSMNodeBuilderIncompleteException () {
		OSMNode.Builder nodeBuilder = newOSMNodeBuilder();

		nodeBuilder.setIncomplete();
		assertTrue(nodeBuilder.isIncomplete());

		nodeBuilder.build();
	}
}
