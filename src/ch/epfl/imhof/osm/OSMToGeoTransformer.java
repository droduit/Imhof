package ch.epfl.imhof.osm;

import java.util.List;

import ch.epfl.imhof.Attributed;
import ch.epfl.imhof.Attributes;
import ch.epfl.imhof.Map;
import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.projection.Projection;
import ch.epfl.imhof.geometry.*;

/**
 * Représente un convertisseur de données OSM en carte
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMToGeoTransformer {
    /**
     * Construit un convertisseur OSM en géométrie qui utilise la projection donnée
     * @param projection
     */
    public OSMToGeoTransformer(Projection projection) {
        
    }
    /**
     * Convertit une carte OSM en une carte géométrique projetée
     * @param map
     * @return
     */
    public Map transform(OSMMap map) {
        return null;
    }
    
    /**
     * Calcule et retourne l'ensemble des anneaux de la relation donnée ayant le rôle spécifié.
     * Cette méthode retourne une liste vide si le calcul des anneaux échoue.
     * @param relation
     * @param role
     * @return
     */
    private List<ClosedPolyLine> ringsForRole(OSMRelation relation, String role) {
        return null;
    }
    /**
     * Calcule et retourne la liste des polygones attribués de la relation donnée, en leur attachant les attributs donnés.
     * @param relation
     * @param attributes
     * @return
     */
    private List<Attributed<Polygon>> assemblePolygon(OSMRelation relation, Attributes attributes) {
        return null;
    }

}
