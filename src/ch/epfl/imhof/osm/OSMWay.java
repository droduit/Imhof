package ch.epfl.imhof.osm;

import ch.epfl.imhof.Attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente un chemin OSM
 * 
 * @author Dominique Roduit (234868)
 * @author Thierry Treyer (235116)
 *
 */
public final class OSMWay extends OSMEntity {

    private final List<OSMNode> nodes;

    /**
     * Sert de bâtisseur à la classe OSMWay et permet de construire un chemin en
     * plusieurs étapes
     * 
     * @author Dominique Roduit (234868)
     * @author Thierry Treyer (235116)
     *
     */
    public static final class Builder extends OSMEntity.Builder {
        private final List<OSMNode> nodes = new ArrayList<>();

        /**
         * Construit un bâtisseur pour un chemin ayant l'identifiant donné.
         * 
         * @param id
         *            Identifiant unique du chemin en construction
         */
        public Builder (long id) {
            super(id);
        }

        /**
         * Ajoute un nœud à (la fin) des nœuds du chemin en cours de
         * construction
         * 
         * @param newNode
         *            Nouveau noeud a ajouter a la liste pour le chemin
         */
        public void addNode (OSMNode newNode) {
            this.nodes.add(newNode);
        }

        /**
         * Permet de déterminer si la construction du chemin est incomplète ou
         * non.
         * 
         * @return true : si le nombre de noeud formant le chemin est inférieur à 2 ou s'il est marqué comme étant incomplet
         */
        public boolean isIncomplete () {
            return super.isIncomplete() || this.nodes.size() < 2;
        }

        /**
         * Construit et retourne le chemin ayant les nœuds et les attributs
         * ajoutés jusqu'à présent. Lève l'exception IllegalStateException si le
         * chemin en cours de construction est incomplet.
         * 
         * @return Le chemin construit a partir de la liste des noeuds ajoutés
         * @throws IllegalStateException
         *             Si le chemin en cours de construction est incomplet
         */
        public OSMWay build () {
            if (this.isIncomplete())
                throw new IllegalStateException(
                        "La liste des noeuds doit etre >= 2 sinon ce n'est pas un chemin !");

            return new OSMWay(super.id(), this.nodes, super.attributes());
        }
    }

    /**
     * Construit un chemin étant donnés son identifiant unique, ses nœuds et ses
     * attributs. Lève l'exception IllegalArgumentException si la liste de nœuds
     * possède moins de deux éléments.
     * 
     * @param id
     *            Identifiant unique attribué au chemin
     * @param nodes
     *            Liste de noeuds représentant le chemin
     * @param attributes
     *            Attributs attachés au chemin
     * @throws IllegalArgumentException
     *             Si la liste des noeuds composants le chemin contient moins de
     *             2 noeuds
     */
    public OSMWay (long id, List<OSMNode> nodes, Attributes attributes) {
        super(id, attributes);

        if (nodes.size() < 2)
            throw new IllegalArgumentException(
                    "La liste des noeuds doit contenir au minimum 2 noeuds");

        this.nodes = Collections.unmodifiableList(new ArrayList<>(nodes));

    }

    /**
     * Retourne le nombre de nœuds du chemin.
     * 
     * @return Nombre de nœuds du chemin
     */
    public int nodesCount () {
        return this.nodes.size();
    }

    /**
     * Retourne la liste des nœuds du chemin.
     * 
     * @return Les nœuds du chemin
     */
    public List<OSMNode> nodes () {
        return this.nodes;
    }

    /**
     * Retourne la liste des nœuds du chemin sans le dernier si celui-ci est
     * identique au premier
     * 
     * @return Liste des noeuds du chemin sans le dernier si celui-ci est
     *         identique au premier
     */
    public List<OSMNode> nonRepeatingNodes () {
        List<OSMNode> n = new ArrayList<>(this.nodes);

        if (this.isClosed())
            n.remove(nodesCount() - 1);

        return Collections.unmodifiableList(n);
    }

    /**
     * Retourne le premier nœud du chemin.
     * 
     * @return Le premier nœud du chemin
     */
    public OSMNode firstNode () {
        return this.nodes.get(0);
    }

    /**
     * Retourne le dernier nœud du chemin.
     * 
     * @return Dernier nœud du chemin
     */
    public OSMNode lastNode () {
        return this.nodes.get(nodesCount() - 1);
    }

    /**
     * Retourne vrai ssi le chemin est fermé, c-à-d que son premier nœud est
     * identique à son dernier nœud.
     * 
     * @return true : si le premier noeud du chemin est identique au dernier
     */
    public boolean isClosed () {
        return this.firstNode().equals(this.lastNode());
    }

}
