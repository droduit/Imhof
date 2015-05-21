package ch.epfl.imhof.geometry;

import java.util.function.Function;

/**
 * Un point à la surface de la Terre, en coordonnées cartésiennes.
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class Point {

    private final double x;
    private final double y;

    /**
     * Construit un point avec l'abscisse et l'ordonnée données.
     * 
     * @param x
     *            L'abscisse du point
     * @param y
     *            L'ordonnée du point
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne la coordonnée de l'abscisse.
     *
     * @return La coordonnées x de l'abscisse
     */
    public double x() {
        return this.x;
    }

    /**
     * Retourne la coordonnée de l'ordonnée.
     *
     * @return La coordonnées y de l'ordonnée
     */
    public double y() {
        return this.y;
    }

    /**
     * Retourne le changement de repère étant donnés deux paires de points dans
     * 2 repères différents
     * 
     * @param p1a
     *            Point 1 dans le repère a
     * @param p2a
     *            Point 2 dans le repère a
     * @param p1b
     *            Point 1 dans le repère b
     * @param p2b
     *            Point 2 dans le repère b
     * @return Changement de repère correspondant
     */
    public static Function<Point, Point> alignedCoordinateChange(Point p1a,
            Point p1b, Point p2a, Point p2b) {
        if (p1a.x() == p2a.x() || p1a.y() == p2a.y())
            throw new IllegalArgumentException(
                    "Le point 1 et 2 sont alignés. Il est donc impossible de définir un changement de repère");

        double ax = (p1b.x - p2b.x) / (p1a.x - p2a.x);
        double bx = (p1a.x * p2b.x - p2a.x * p1b.x) / (p1a.x - p2a.x);

        double ay = (p1b.y - p2b.y) / (p1a.y - p2a.y);
        double by = (p1a.y * p2b.y - p2a.y * p1b.y) / (p1a.y - p2a.y);

        return (p) -> new Point(ax * p.x + bx, ay * p.y + by);
    }
}
