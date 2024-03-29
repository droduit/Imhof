package ch.epfl.imhof.geometry;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Une polyligne immuable, formée par une liste de points.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public abstract class PolyLine {
    private final List<Point> points;

    /**
     * Construit une PolyLine avec les points donnés.
     *
     * @param points
     *            La liste des points composants la PolyLine
     * @throws IllegalArgumentException
     *             Si la liste de points est vide
     */
    public PolyLine(List<Point> points) {
        if (!areValidPoints(points))
            throw new IllegalArgumentException("Invalid list of points");

        this.points = Collections.unmodifiableList(new ArrayList<>(points));
    }

    /**
     * Retourne la liste des points composants la PolyLine.
     *
     * @return Les points de la PolyLine
     */
    public List<Point> points() {
        return this.points;
    }

    /**
     * Retourne le premier point de la PolyLine
     *
     * @return Le premier point de la PolyLine
     */
    public Point firstPoint() {
        return this.points.get(0);
    }

    /**
     * Vérifie que la liste de points passée en paramètre correspond aux
     * critères nécessaires à une PolyLine. C.a.d. : la liste n'est pas null et
     * la liste contient au moins un élément.
     *
     * @return True si la liste correspond aux critères
     */
    private boolean areValidPoints(List<Point> points) {
        return (points != null && points.size() > 0);
    }

    /**
     * Permet de déterminer si la PolyLine est fermée ou ouverte.
     *
     * @return true : si la PolyLine est fermée
     */
    public abstract boolean isClosed();

    /**
     * Sert de bâtisseur aux deux sous-classes de PolyLine et permet de
     * construire une polyligne en plusieurs étapes
     *
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     */
    public static class Builder {
        /** La liste des points de la future PolyLine. */
        private final List<Point> points = new LinkedList<>();

        /**
         * Ajoute un point à la liste des points de la future PolyLine.
         *
         * @param point
         *            Le point à ajouter à la liste
         */
        public void addPoint(Point p) {
            this.points.add(p);
        }

        /**
         * Finalise la construction de la PolyLine en type ouverte.
         *
         * @return Un objet OpenPolyLine initialisé avec les points ajoutés
         */
        public OpenPolyLine buildOpen() {
            return new OpenPolyLine(this.points);
        }

        /**
         * Finalise la construction de la PolyLine en type fermée.
         *
         * @return Un objet ClosedPolyLine initialisé avec les points ajoutés
         */
        public ClosedPolyLine buildClosed() {
            return new ClosedPolyLine(this.points);
        }
    }
}
