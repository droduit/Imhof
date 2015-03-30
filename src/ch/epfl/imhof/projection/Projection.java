package ch.epfl.imhof.projection;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.PointGeo;

/**
 * Définition des méthodes permettant de projetter des points en coordonnées
 * géographiques vers des points en coordonnées cartésiennes et inversément.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public interface Projection {
    /**
     * Projection d'un point en coordonnées géographiques vers un point en
     * coordonnées cartésiennes.
     *
     * @param point
     *            Un point en coordonnées géographiques
     *
     * @return Le point projetté en coordonnées cartésiennes
     */
    public Point project (PointGeo point);

    /**
     * Projection d'un point en coordonnées cartésiennes vers un point en
     * coordonnées géographiques.
     *
     * @param point
     *            Un point en coordonnées cartésiennes
     *
     * @return Le point projetté en coordonnées géographiques
     */
    public PointGeo inverse (Point point);
}
