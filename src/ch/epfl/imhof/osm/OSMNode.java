package ch.epfl.imhof.osm;

import java.util.Objects;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.Attributes;

/**
 * Représente un nœud OSM
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMNode extends OSMEntity {
    /**
     * Sert de bâtisseur à la classe OSMNode et permet de construire un nœud en plusieurs étapes
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     */
	public final static class Builder extends OSMEntity.Builder {
		private PointGeo position;

		/**
		 * Construit un bâtisseur pour un nœud ayant l'identifiant et la position donnés.
		 * @param id Identifiant unique attaché au noeud
		 * @param position Position géographique du noeud
		 */
		public Builder (long id, PointGeo position) {
			super(id);

			this.position = Objects.requireNonNull(position, "position ne doit pas être null");
		}

		/**
		 * Construit un nœud OSM avec l'identifiant et la position passés au constructeur,
		 * et les éventuels attributs ajoutés jusqu'ici au bâtisseur.
		 * @return Noeud construit sur la base des informatoins ajoutés jusqu'ici au bâtisseur
		 * @throws IllegalStateException Lorsque le noeud est encore incomplet
		 */
		public OSMNode build () {
			if (this.isIncomplete())
				throw new IllegalStateException("noeud incomplet");

			return new OSMNode(this.id(), this.position, this.attributes());
		}
	}

	private PointGeo position;

	/**
	 * Retourne la position du nœud
	 * @return Position du nœud
	 */
	public PointGeo position () { return this.position; }

	/**
	 * Construit un nœud OSM avec l'identifiant, la position et les attributs donnés
	 * @param id Identifiant unique associé au noeud
	 * @param position Position géographique du noeud
	 * @param attributes Attributs attachés au noeud
	 */
	public OSMNode (long id, PointGeo position, Attributes attributes) {
		super(id, attributes);

		this.position = Objects.requireNonNull(position, "position ne doit pas être null");
	}
}
