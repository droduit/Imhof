package ch.epfl.imhof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;

public final class Map {

    private final List<Attributed<PolyLine>> polylines;
    private final List<Attributed<Polygon>> polygons;
    
    /**
     * Construit une carte à partir des listes de polylignes et polygones attribués donnés
     * @param polylines Liste de polylignes attribuées
     * @param polygons Liste de polygones attribués
     */
    public Map(List<Attributed<PolyLine>> polylines, List<Attributed<Polygon>> polygons) {
        this.polylines = Collections.unmodifiableList(new ArrayList<>(polylines));
        this.polygons = Collections.unmodifiableList(new ArrayList<>(polygons));
    }
    
    /**
     * Retourne la liste des polylignes attribuées de la carte
     * @return Liste des polylignes attribuées de la carte
     */
    public List<Attributed<PolyLine>> polyLines() {
        return polylines;
    }
    
    /**
     * Retourne la liste des polygones attribués de la carte
     * @return Liste des polygones attribués de la carte
     */
    public List<Attributed<Polygon>> polygons() {
        return polygons;
    }
    
    public static class Builder {
        private final List<Attributed<PolyLine>> polylines;
        private final List<Attributed<Polygon>> polygons;
        
        public Builder() {
            polylines = new ArrayList<>();
            polygons = new ArrayList<>();
        }
        
        public void addPolyLine(Attributed<PolyLine> newPolyLine) {
            polylines.add(newPolyLine);
        }
        public void addPolygon(Attributed<Polygon> newPolygon) {
            polygons.add(newPolygon);
        }
        public Map build() {
            return new Map(polylines, polygons);
        }
    }
    
}
