package ch.epfl.imhof;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Représente un graphe non orienté générique.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 * 
 * @param <N> Type des noeuds du graphe
 */

public final class Graph<N> {
    public Map<N, Set<N>> neighbors;

    /**
     * Construit un graphe non orienté immuable à partir de la table d'adjacence donnée
     *
     * @param neighbors
     *            Un map contenant les nœuds du graphe comme clés et les voisins
     *            comme valeurs
     */
    public Graph (Map<N, Set<N>> neighbors) {
        HashMap<N, Set<N>> nbs = new HashMap<N, Set<N>>();

        for (N node : neighbors.keySet())
            nbs.put(node, Collections.unmodifiableSet(new HashSet<N>(neighbors.get(node))));

        this.neighbors = Collections.unmodifiableMap(nbs);
    }

    /**
     * Retourne la liste des nœuds composants le graphe.
     *
     * @return Les nœuds du graphe
     */
    public Set<N> nodes () {
        return this.neighbors.keySet();
    }

    /**
     * Retourne la liste des voisins du nœud demandé.
     *
     * @param node
     *            Le nœud dont on cherche les voisins
     * @throws IllegalArgumentException
     *             Si le nœud demandé n'existe pas dans le graph
     *
     * @return La liste des voisins du nœud demandé
     */
    public Set<N> neighborsOf (N node) {
        if (!this.neighbors.containsKey(node))
            throw new IllegalArgumentException("Le nœud demandé est inconnu");

        return this.neighbors.get(node);
    }
    
    /**
     * Bâtisseur générique de la classe Graph
     * 
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     * @param <N> Type des noeuds du bâtisseur
     */
    public static final class Builder<N> {
        public Map<N, Set<N>> neighbors = new HashMap<N, Set<N>>();

        /**
         * Ajoute le nœud donné au graphe en cours de construction, s'il n'en faisait pas déjà partie.
         *
         * @param node
         *            Le nœud à ajouter au graphe
         */
        public void addNode (N node) {
            if (!this.neighbors.containsKey(node))
                this.neighbors.put(node, new HashSet<N>());
        }

        /**
         * Ajoute une arête entre les deux noeuds donnés au graphe en cours de construction.
         *
         * @param n1
         *            Le premier nœud du lien
         * @param n2
         *            Le deuxième nœud du lien
         *
         * @throws IllegalArgumentException
         *             Si n1 ou n2 n'existent pas dans le graph
         */
        public void addEdge (N n1, N n2) {
            if (!this.neighbors.containsKey(n1))
                throw new IllegalArgumentException("Le nœud n1 est inconnu");

            if (!this.neighbors.containsKey(n2))
                throw new IllegalArgumentException("Le nœud n2 est inconnu");

            this.neighbors.get(n1).add(n2);
            this.neighbors.get(n2).add(n1);
        }

        /**
         * Construit le graphe composé des noeuds et arêtes ajoutés jusqu'à présent au bâtisseur
         *
         * @return Le graphe construit avec les données reçues
         */
        public Graph<N> build () {
            return new Graph<N>(this.neighbors);
        }
    }
}
