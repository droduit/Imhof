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
     * Retourne le changement de repère étant donnés deux paires de points dans 2 repères différents
     * @param p1a Point 1 dans le repère a
     * @param p2a Point 2 dans le repère a
     * @param p1b Point 1 dans le repère b
     * @param p2b Point 2 dans le repère b
     * @return Changement de repère correspondant
     */
    public static Function<Point, Point> alignedCoordinateChange(Point p1a, Point p2a, Point p1b, Point p2b) {
        if(p1a.x() == p1b.x() || p1a.y() == p1b.y())
            throw new IllegalArgumentException("Le point 1 est aligné dans les 2 repères. Il est donc impossible de définir un changement de repère");
        if(p2a.x() == p2b.x() || p2a.y() == p2b.y())
            throw new IllegalArgumentException("Le point 2 est aligné dans les 2 repères. Il est donc impossible de définir un changement de repère");
        
        Function<Point, Point> chRepere = p -> {
            double newX = p.x(); // TODO Formule pour le changement de repère
            double newY = p.y();
            return new Point(newX, newY);
        };
        return chRepere;
    }
}
