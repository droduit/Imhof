package ch.epfl.imhof.osm;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente une carte OpenStreetMap, c'est-à-dire un ensemble de chemins et de relations
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMMap {
    private final List<OSMWay> ways;
    private final List<OSMRelation> relations;
    
    /**
     * Bâtisseur de la classe OSMMap. 
     * Stocke les entitées et permet de les récupérer
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     */
    public static final class Builder {
        private Map<Long, OSMNode> nodes = new HashMap<>();
        private Map<Long, OSMWay> ways = new HashMap<>();
        private Map<Long, OSMRelation> relations = new HashMap<>();
        
        /**
         * Ajoute le nœud donné au bâtisseur
         * @param newNode Nouveau noeud à ajouter au bâtisseur
         */
        public void addNode(OSMNode newNode) {
            if(newNode!=null)
                nodes.put(newNode.id(),  newNode);
        }
        
        /**
         * Retourne le nœud dont l'identifiant unique est égal à celui donné,
         * ou null si ce nœud n'a pas été ajouté précédemment au bâtisseur
         * @param id Identifiant unique du noeud
         * @return le noeud correspondant à l'identifiant passé
         */
        public OSMNode nodeForId(long id){
            return nodes.get(id);
        }
        
        /**
         * Ajoute le chemin donné à la carte en cours de construction
         * @param newWay Nouveau chemin à ajouter à la Map
         */
        public void addWay(OSMWay newWay) {
            ways.put(newWay.id(), Objects.requireNonNull(newWay));
        }
        
        /**
         * Retourne le chemin dont l'identifiant unique est égal à celui donné,
         * ou null si ce chemin n'a pas été ajouté précédemment au bâtisseur
         * @param id Identifiant unique du chemin que l'on veut recupérer
         * @return Chemin correspondant à l'identifiant passé en paramètre
         */
        public OSMWay wayForId(long id){
            return ways.get(id);
        }
        
        /**
         * Ajoute la relation donnée à la carte en cours de construction
         * @param newRelation Nouvelle relation à ajouter à la carte
         */
        public void addRelation(OSMRelation newRelation) {
            relations.put(newRelation.id(), Objects.requireNonNull(newRelation));
        }
        
        /**
         * Retourne la relation dont l'identifiant unique est égal à celui donné,
         * ou null si cette relation n'a pas été ajoutée précédemment au bâtisseur
         * @param id Identifiant unique de la relation à récupérer
         * @return La relation correspondant à l'identifiant passé en paramètre
         */
        public OSMRelation relationForId(long id) {
            return relations.get(id);
        }
        
        /**
         * Construit une carte OSM avec les chemins et les relations ajoutés jusqu'à présent.
         * @return Carte OSM construite sur la base des chemins et relations ajoutés jusqu'à présent
         */
        public OSMMap build() {
            return new OSMMap(ways.values(), relations.values());
        }
    }
 
    
    /**
     * Construit une carte OSM avec les chemins et les relations donnés.
     * @param ways Chemins 
     * @param relations Relations
     */
    public OSMMap(Collection<OSMWay> ways, Collection<OSMRelation> relations) {
        this.ways = Collections.unmodifiableList(new ArrayList<OSMWay>(ways));
        this.relations = Collections.unmodifiableList(new ArrayList<OSMRelation>(relations));
    }
    
     /**
     * Retourne la liste des chemins de la carte.
     * @return Liste des chemins de la carte
     */
    public List<OSMWay> ways() {
        return this.ways;
    }
    
    /**
     * Retourne la liste des relations de la carte.
     * @return Liste des relations de la carte
     */
    public List<OSMRelation> relations() {
        return this.relations;
    }
  
}
