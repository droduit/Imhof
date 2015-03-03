package ch.epfl.imhof.osm;

import org.junit.*;
import static org.junit.Assert.*;

import static ch.epfl.imhof.TestUtil.*;

import java.util.List;

import static ch.epfl.imhof.osm.OSMRelation.Member.Type.*;

public class OSMRelationTest {
	private void assertMemberEquals (OSMRelation.Member expected, OSMRelation.Member member) {
		assertEquals(expected.type(), member.type());
		assertEquals(expected.role(), member.role());
		assertEquals(expected.member(), member.member());
	}

    @Test
    public void testOSMRelationMember () {
		OSMNode node = newOSMNode();
		OSMRelation.Member member = new OSMRelation.Member(NODE, DEFAULT_ROLE, node);

		assertNotNull(member);
		assertEquals(NODE, member.type());
		assertEquals(DEFAULT_ROLE, member.role());
		assertTrue(node == member.member());
		assertNodeEquals(node, (OSMNode)member.member());
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
