package ch.epfl.imhof.projection;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.PointGeo;

/**
 * Implémentation de la projection équirectangulaire.
 * Cette projection utilise la longitude et la latitude comme
 * coordonnées cartésiennes.
 * Cependant, cette projection produit de fortes déformations et
 * est à éviter.
 *
 * @author Thierry Treyer (235116)
 */
public final class EquirectangularProjection implements Projection {
    public static Point Project (PointGeo pg) {
        return new Point(pg.longitude(), pg.latitude());
    }

    public static PointGeo Inverse (Point p) {
        return new PointGeo(p.x(), p.y());
    }

    public Point project (PointGeo pg) {
        return EquirectangularProjection.Project(pg);
    }

    public PointGeo inverse (Point p) {
        return EquirectangularProjection.Inverse(p);
    }
}
