package ch.epfl.imhof.osm;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.Attributes;

public class OSMRelationTest {
	private static int ID = 1;

	private static final double DOUBLE_DELTA = 0.000001;
	private static final String ATTR_STRING_KEY = "foo";
	private static final String ATTR_STRING_VALUE = "bar";
	private static final String ATTR_INT_KEY = "int";
	private static final String ATTR_INT_VALUE = "1337";
	private static final String UNKNOWN_KEY = "randomkey";

	private static final PointGeo DEFAULT_POSITION = new PointGeo(1.2, 0.7);
	private static final OSMNode MEMBER_NODE = newNode();
	private static final OSMRelation.Member.Type MEMBER_NODE_TYPE = OSMRelation.Member.Type.NODE;
	private static final OSMRelation.Member.Type MEMBER_WAY_TYPE = OSMRelation.Member.Type.WAY;
	private static final OSMRelation.Member.Type MEMBER_RELATION_TYPE = OSMRelation.Member.Type.RELATION;
	private static final String MEMBER_ROLE = "testing";

	private static Attributes newAttributes () {
		Attributes.Builder ab = new Attributes.Builder();

		ab.put(ATTR_STRING_KEY, ATTR_STRING_VALUE);
		ab.put(ATTR_INT_KEY, ATTR_INT_VALUE);

		return ab.build();
	}

	private static int newId () { return ID++; }

	private static OSMNode newNode () {
		return new OSMNode(newId(), DEFAULT_POSITION, newAttributes());
	}

	private static List<OSMNode> newNodeList () {
		LinkedList<OSMNode> nodeList = new LinkedList<OSMNode>();

		nodeList.add(newNode());
		nodeList.add(newNode());
		nodeList.add(newNode());

		return nodeList;
	}

	private static OSMWay newWay () {
		return new OSMWay(newId(), newNodeList(), newAttributes());
	}

	private static List<OSMRelation.Member> newSimpleMemberList () {
		List<OSMRelation.Member> members = new LinkedList<OSMRelation.Member>();

		members.add(new OSMRelation.Member(MEMBER_NODE_TYPE, MEMBER_ROLE, newNode()));
		members.add(new OSMRelation.Member(MEMBER_WAY_TYPE, MEMBER_ROLE, newWay()));

		return members;
	}

	private static OSMRelation newSimpleRelation () {
		return new OSMRelation(newId(), newSimpleMemberList(), newAttributes());
	}

	private static List<OSMRelation.Member> newMemberList () {
		List<OSMRelation.Member> members = new LinkedList<OSMRelation.Member>();

		members.add(new OSMRelation.Member(MEMBER_NODE_TYPE, MEMBER_ROLE, newNode()));
		members.add(new OSMRelation.Member(MEMBER_WAY_TYPE, MEMBER_ROLE, newWay()));
		members.add(new OSMRelation.Member(MEMBER_RELATION_TYPE, MEMBER_ROLE, newSimpleRelation()));

		return members;
	}

	private static OSMRelation newRelation () {
		return new OSMRelation(newId(), newMemberList(), newAttributes());
	}

	private void assertAttributesEquals (Attributes expected, Attributes attributes, Set<String> keys) {
		assertEquals(expected.size(), attributes.size());

		for (String key : keys)
			assertEquals(expected.get(key), attributes.get(key));
	}

	private void assertPositionEquals (PointGeo expected, PointGeo point) {
		assertEquals(expected.longitude(), point.longitude(), DOUBLE_DELTA);
		assertEquals(expected.latitude(), point.latitude(), DOUBLE_DELTA);
	}

	private void assertNodeEquals (OSMNode expected, OSMNode node) {
		Set<String> keys = new HashSet<String>();
		keys.add(ATTR_STRING_KEY);
		keys.add(ATTR_INT_KEY);

		assertEquals(expected.id(), node.id());

		assertEquals(expected.attributes(), node.attributes());
		assertAttributesEquals(expected.attributes(), node.attributes(), keys);

		assertEquals(expected.position(), node.position());
		assertPositionEquals(expected.position(), node.position());
	}

	private void assertMemberEquals (OSMRelation.Member expected, OSMRelation.Member member) {
		assertEquals(expected.type(), member.type());
		assertEquals(expected.role(), member.role());
		assertEquals(expected.member(), member.member());
	}

    @Before
    public void setUp() {
    }

    @Test
    public void testOSMRelationMember () {
		OSMRelation.Member member = new OSMRelation.Member(MEMBER_NODE_TYPE, MEMBER_ROLE, MEMBER_NODE);

		assertNotNull(member);
		assertEquals(MEMBER_NODE_TYPE, member.type());
		assertEquals(MEMBER_ROLE, member.role());
		assertEquals(MEMBER_NODE, member.member());
		assertNodeEquals(MEMBER_NODE, (OSMNode)member.member());
    }

	@Test
	public void testOSMRelationBuilder () {
		List<OSMRelation.Member> members = newMemberList();

		OSMRelation.Builder builder = new OSMRelation.Builder(ID);

		for (OSMRelation.Member m : members)
			builder.addMember(m.type(), m.role(), m.member());

		OSMRelation relation = builder.build();
		List<OSMRelation.Member> relationMembers = relation.members();

		assertEquals(members.size(), relationMembers.size());

		for (int i = 0, l = members.size(); i < l; i++)
			assertMemberEquals(members.get(i), relationMembers.get(i));
	}

	@Test (expected = IllegalStateException.class)
	public void testOSMRelationBuilderException () {
		List<OSMRelation.Member> members = newMemberList();

		OSMRelation.Builder builder = new OSMRelation.Builder(ID);

		for (OSMRelation.Member m : members)
			builder.addMember(m.type(), m.role(), m.member());

		builder.setIncomplete();

		builder.build();
	}

	@Test
	public void testMembers () {
		List<OSMRelation.Member> members = newMemberList();
		OSMRelation relation = new OSMRelation(newId(), members, newAttributes());

		List<OSMRelation.Member> relationMembers = relation.members();


		assertTrue(members != relation.members());

		for (int i = 0, l = members.size(); i < l; i++)
			assertEquals(members.get(i), relationMembers.get(i));
	}
}
