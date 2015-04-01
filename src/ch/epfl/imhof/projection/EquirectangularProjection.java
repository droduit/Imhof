package ch.epfl.imhof.projection;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.PointGeo;

/**
 * Implémentation de la projection équirectangulaire. Cette projection utilise
 * la longitude et la latitude comme coordonnées cartésiennes. Cependant, cette
 * projection produit de fortes déformations et est à éviter.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class EquirectangularProjection implements Projection {
    /**
     * Projection equirectangulaire d'un point géographique
     */
    public Point project(PointGeo pg) {
        return new Point(pg.longitude(), pg.latitude());
    }

    /**
     * Dé-projection équirectangulaire d'un Point geographique
     */
    public PointGeo inverse(Point p) {
        return new PointGeo(p.x(), p.y());
    }
}
