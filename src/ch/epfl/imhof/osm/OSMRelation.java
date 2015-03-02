package ch.epfl.imhof.osm;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

import ch.epfl.imhof.Attributes;

public final class OSMRelation extends OSMEntity {
	public final static class Member {
		public static enum Type {
			NODE, WAY, RELATION
		}

		private Type type;
		private String role;
		private OSMEntity member;

		public Type type () { return this.type; }
		public String role () { return this.role; }
		public OSMEntity member () { return this.member; }

		public Member (Type type, String role, OSMEntity member) {
			this.type = Objects.requireNonNull(type, "type ne peut pas être null");
			this.role = Objects.requireNonNull(role, "role ne peut pas être null");
			this.member = Objects.requireNonNull(member, "member ne peut pas être null");
		}
	}

	public final static class Builder extends OSMEntity.Builder {
		private LinkedList<Member> members = new LinkedList<Member>();

		public Builder (long id) { super(id); }

		public void addMember (Member.Type type, String role, OSMEntity newMember) {
			this.members.add(new Member(type, role, newMember));
		}

		public OSMRelation build () {
			if (this.isIncomplete())
				throw new IllegalStateException("relation incomplète"); 

			return new OSMRelation(this.id(), this.members, this.attributes());
		}
	}

	private List<Member> members;

	public List<Member> members () { return this.members; }

	public OSMRelation (long id, List<Member> members, Attributes attributes) {
		super(id, attributes);

		this.members = Collections.unmodifiableList(new ArrayList<Member>(members));
	}
}
