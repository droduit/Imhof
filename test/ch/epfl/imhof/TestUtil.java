package ch.epfl.imhof;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import ch.epfl.imhof.*;
import ch.epfl.imhof.osm.*;
import static ch.epfl.imhof.osm.OSMRelation.Member.Type.*;

public abstract class TestUtil {
	public static final double DOUBLE_DELTA = 10e-6;
	public static final String DEFAULT_ROLE = "testing";

	public static int ID = 1;

	/** Méthode de créations */
	public static Map<String, String> newMap () {
		Map<String, String> m = new HashMap<String, String>();

		m.put("foo", "bar");
		m.put("int", "1337");
		m.put("float", "3.14159");

		return m;
	}

	public static int newId () {
		return ID++;
	}
	
	public static long newLongId() {
	    return (long)(++ID);
	}

	public static Attributes newAttributes (Map<String, String> attrs) {
		Attributes.Builder ab = new Attributes.Builder();

		for (String key : attrs.keySet())
			ab.put(key, attrs.get(key));

		return ab.build();
	}

	public static Attributes newAttributes () {
		return newAttributes(newMap());
	}

	public static PointGeo newPointGeo (double longitude, double latitude) {
		return new PointGeo(longitude, latitude);
	}

	public static PointGeo newPointGeo () {
		double longitude = (Math.random() * Math.PI * 2) - Math.PI;
		double latitude = (Math.random() * Math.PI) - (Math.PI / 2);

		return newPointGeo(longitude, latitude);
	}

	public static OSMNode newOSMNode () {
		return new OSMNode(newId(), newPointGeo(), newAttributes());
	}

	public static List<OSMNode> newOSMNodeList () {
		LinkedList<OSMNode> nodeList = new LinkedList<OSMNode>();

		nodeList.add(newOSMNode());
		nodeList.add(newOSMNode());
		nodeList.add(newOSMNode());

		return nodeList;
	}

	public static OSMWay newOSMWay () {
		return new OSMWay(newId(), newOSMNodeList(), newAttributes());
	}

	public static List<OSMRelation.Member> newSimpleOSMMemberList () {
		List<OSMRelation.Member> members = new LinkedList<OSMRelation.Member>();

		members.add(new OSMRelation.Member(NODE, DEFAULT_ROLE, newOSMNode()));
		members.add(new OSMRelation.Member(WAY, DEFAULT_ROLE, newOSMWay()));

		return members;
	}

	public static OSMRelation newSimpleOSMRelation () {
		return new OSMRelation(newId(), newSimpleOSMMemberList(), newAttributes());
	}

	public static List<OSMRelation.Member> newMemberList () {
		List<OSMRelation.Member> members = new LinkedList<OSMRelation.Member>();

		members.add(new OSMRelation.Member(NODE, DEFAULT_ROLE, newOSMNode()));
		members.add(new OSMRelation.Member(WAY, DEFAULT_ROLE, newOSMWay()));
		members.add(new OSMRelation.Member(RELATION, DEFAULT_ROLE, newSimpleOSMRelation()));

		return members;
	}

	/** Méthodes d'assertion */
	public static void assertPositionEquals (PointGeo expected, PointGeo point) {
		assertEquals(expected.longitude(), point.longitude(), DOUBLE_DELTA);
		assertEquals(expected.latitude(), point.latitude(), DOUBLE_DELTA);
	}

	public static void assertAttributesEquals (Attributes expected, Attributes attributes, Set<String> keys) {
		assertEquals(expected.size(), attributes.size());

		for (String key : keys)
			assertEquals(expected.get(key), attributes.get(key));
	}

	public static void assertAttributesEquals (Attributes expected, Attributes attributes, Map<String, String> attrs) {
		assertAttributesEquals(expected, attributes, attrs.keySet());
	}

	public static void assertAttributesEquals (Attributes expected, Attributes attributes) {
		assertAttributesEquals(expected, attributes, newMap());
	}

	public static void assertNodeEquals (OSMNode expected, OSMNode node, Set<String> attrKeys) {
		assertEquals(expected.id(), node.id());
		assertPositionEquals(expected.position(), node.position());
		assertAttributesEquals(expected.attributes(), node.attributes(), attrKeys);
	}

	public static void assertNodeEquals (OSMNode expected, OSMNode node, Map<String, String> attrs) {
		assertNodeEquals(expected, node, attrs.keySet());
	}

	public static void assertNodeEquals (OSMNode expected, OSMNode node) {
		assertNodeEquals(expected, node, newMap());
	}
}
