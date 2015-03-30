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
 */
public final class Graph<N> {
    public Map<N, Set<N>> neighbors;

    /**
     * Le constructeur de la class Graph.
     */
    public static final class Builder<N> {
        public Map<N, Set<N>> neighbors = new HashMap<N, Set<N>>();

        /**
         * Ajoute un nœud au graph.
         *
         * @param node
         *            Le nœud à ajouter au graph
         */
        public void addNode (N node) {
            if (this.neighbors.containsKey(node) == false)
                this.neighbors.put(node, new HashSet<N>());
        }

        /**
         * Définit un lien entre deux nœuds du graph.
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
            if (this.neighbors.containsKey(n1) == false)
                throw new IllegalArgumentException("Le nœud n1 est inconnu");

            if (this.neighbors.containsKey(n2) == false)
                throw new IllegalArgumentException("Le nœud n2 est inconnu");

            this.neighbors.get(n1).add(n2);
            this.neighbors.get(n2).add(n1);
        }

        /**
         * Construit un Graph avec les éléments donnés précédemment.
         *
         * @return Le graph construit avec les données reçues
         */
        public Graph<N> build () {
            return new Graph<N>(this.neighbors);
        }
    }

    /**
     * Construit un graph immuable à partir d'un Map.
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
     * Retourne la liste des voisin du nœud demandé.
     *
     * @param node
     *            Le nœud dont on cherche les voisins
     * @throws IllegalArgumentException
     *             Si le nœud demandé n'existe pas dans le graph
     *
     * @return La list des voisins du nœud demandé
     */
    public Set<N> neighborsOf (N node) {
        if (this.neighbors.containsKey(node) == false)
            throw new IllegalArgumentException("Le nœud demandé est inconnu");

        return this.neighbors.get(node);
    }
}
