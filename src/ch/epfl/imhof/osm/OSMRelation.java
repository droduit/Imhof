package ch.epfl.imhof.osm;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

import ch.epfl.imhof.Attributes;

/**
 * Représente une relation OSM
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMRelation extends OSMEntity {
    /**
     * Enumère les trois types de membres qu'une relation peut comporter,
     * à savoir NODE pour les nœuds, WAY pour les chemins, et RELATION pour les relations
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     */
	public final static class Member {
		public static enum Type {
			NODE, WAY, RELATION
		}

		private Type type;
		private String role;
		private OSMEntity member;

		/**
		 * Retourne le type du membre.
		 * @return Type du membre
		 */
		public Type type () { return this.type; }
		/**
		 * Retourne le rôle du membre.
		 * @return Rôle du membre
		 */
		public String role () { return this.role; }
		/**
		 * Retourne le membre lui-même.
		 * @return Le membre lui-meme
		 */
		public OSMEntity member () { return this.member; }

		/**
		 * Construit un membre ayant le type, le rôle et la valeur donnés.
		 * @param type Type du membre
		 * @param role  Role du membre
		 * @param member Entité
		 */
		public Member (Type type, String role, OSMEntity member) {
			this.type = Objects.requireNonNull(type, "type ne peut pas être null");
			this.role = Objects.requireNonNull(role, "role ne peut pas être null");
			this.member = Objects.requireNonNull(member, "member ne peut pas être null");
		}
	}
	
	/**
	 * Sert de bâtisseur à la classe OSMRelation et permet de construire une relation en plusieurs étapes.
	 * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
	 *
	 */
	public final static class Builder extends OSMEntity.Builder {
		private LinkedList<Member> members = new LinkedList<Member>();

		/**
		 * Construit un bâtisseur pour une relation ayant l'identifiant donné.
		 * @param id Identifiant unique pour une relation
		 */
		public Builder (long id) { super(id); }

		/**
		 * Ajoute un nouveau membre de type et de rôle donnés à la relation.
		 * @param type Type du membre 
		 * @param role Rôle du membre
		 * @param newMember Nouveau membre que l'on ajoute a la relation
		 */
		public void addMember (Member.Type type, String role, OSMEntity newMember) {
			this.members.add(new Member(type, role, newMember));
		}

		/**
		 * Construit et retourne la relation ayant l'identifiant passé au constructeur
		 * ainsi que les membres et les attributs ajoutés jusqu'à présent au bâtisseur
		 * @return La relation construite sur la base de tout ce qui a été donné jusqu'ici 
		 */
		public OSMRelation build () {
			if (this.isIncomplete())
				throw new IllegalStateException("relation incomplète"); 

			return new OSMRelation(this.id(), this.members, this.attributes());
		}
	}

	private List<Member> members;

	/**
	 * Retourne la liste des membres de la relation.
	 * @return La liste des membres de la relation
	 */
	public List<Member> members () { return this.members; }

	/**
	 * Construit une relation étant donnés son identifiant unique, ses membres et ses attributs
	 * @param id Identifiant unique relatif à la relation
	 * @param members Membres de la relation
	 * @param attributes Attributs attachés à la relation
	 */
	public OSMRelation (long id, List<Member> members, Attributes attributes) {
		super(id, attributes);

		this.members = Collections.unmodifiableList(new ArrayList<Member>(members));
	}
}
