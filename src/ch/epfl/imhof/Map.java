package ch.epfl.imhof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;

/**
 * Représente une carte projetée, composée d'entités géométriques attribuées.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class Map {

    private final List<Attributed<PolyLine>> polylines;
    private final List<Attributed<Polygon>> polygons;

    /**
     * Construit une carte à partir des listes de polylignes et polygones
     * attribués donnés
     *
     * @param polylines
     *            Liste de polylignes attribuées
     * @param polygons
     *            Liste de polygones attribués
     */
    public Map(List<Attributed<PolyLine>> polylines,
            List<Attributed<Polygon>> polygons) {
        this.polylines = Collections
                .unmodifiableList(new ArrayList<>(polylines));
        this.polygons = Collections.unmodifiableList(new ArrayList<>(polygons));
    }

    /**
     * Retourne la liste des polylignes attribuées de la carte
     *
     * @return Liste des polylignes attribuées de la carte
     */
    public List<Attributed<PolyLine>> polyLines() {
        return polylines;
    }

    /**
     * Retourne la liste des polygones attribués de la carte
     *
     * @return Liste des polygones attribués de la carte
     */
    public List<Attributed<Polygon>> polygons() {
        return polygons;
    }

    /**
     * Bâtisseur de la classe Map
     *
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     */
    public static class Builder {
        private final List<Attributed<PolyLine>> polylines;
        private final List<Attributed<Polygon>> polygons;

        /**
         * Constructeur du bâtisseur
         */
        public Builder() {
            polylines = new ArrayList<>();
            polygons = new ArrayList<>();
        }

        /**
         * Ajoute une polyligne attribuée à la carte en cours de construction
         *
         * @param newPolyLine
         *            Polyligne à ajouter à la carte
         */
        public void addPolyLine(Attributed<PolyLine> newPolyLine) {
            polylines.add(Objects.requireNonNull(newPolyLine,
                    "polyline ne peut être null"));
        }

        /**
         * Ajoute un polygone attribué à la carte en cours de construction
         *
         * @param newPolygon
         *            Polygone à ajouter à la carte
         */
        public void addPolygon(Attributed<Polygon> newPolygon) {
            polygons.add(Objects.requireNonNull(newPolygon,
                    "polygon ne peut être null"));
        }

        /**
         * Construit une carte avec les polylignes et polygones ajoutés jusqu'à
         * présent
         *
         * @return Carte avec les polylignes et polygones ajoutés jusqu'à
         *         présent
         */
        public Map build() {
            return new Map(polylines, polygons);
        }
    }

}
