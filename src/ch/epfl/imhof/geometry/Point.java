package ch.epfl.imhof.geometry;

/**
 * Un point à la surface de la Terre, en coordonnées
 * cartésiennes.
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class Point {
    /** L'abscisse du point */
    private final double x;

    /** L'ordonnée du point */
    private final double y;

    /**
     * Retourne la coordonnée de l'abscisse.
     *
     * @return  La coordonnées x de l'abscisse
     */
    public double x () { return this.x; }

    /**
     * Retourne la coordonnée de l'ordonnée.
     *
     * @return  La coordonnées y de l'ordonnée
     */
    public double y () { return this.y; }

    /**
     * Construit un point avec l'abscisse et l'ordonnée données.
     * 
     * @param x     L'abscisse du point
     * @param y     L'ordonnée du point
     */
    public Point (double x, double y) {
        this.x = x;
        this.y = y;
    }
}
