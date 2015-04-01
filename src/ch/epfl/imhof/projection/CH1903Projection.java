package ch.epfl.imhof.projection;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.geometry.Point;

/**
 * Implémentation de la projection CH1903, adaptée pour fonctionner avec des
 * coordonnées WGS84. Cette projection utilise les formules de projection de
 * Mercator, modifée pour se conformer aux coordonnées en CH1903.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class CH1903Projection implements Projection {
    public Point project(PointGeo pg) {
        double lon = Math.toDegrees(pg.longitude());
        double lat = Math.toDegrees(pg.latitude());

        double lon1 = (lon * 3600 - 26782.5) / 10000;
        double lat1 = (lat * 3600 - 169028.66) / 10000;

        double lon2 = lon1 * lon1; // => lon2 = Math.pow(lon1, 2)
        double lon3 = lon2 * lon1; // => lon3 = Math.pow(lon1, 3)
        double lat2 = lat1 * lat1; // => lat2 = Math.pow(lat1, 2)
        double lat3 = lat2 * lat1; // => lat3 = Math.pow(lat1, 3)

        double x = 600072.37 + 211455.93 * lon1 - 10938.51 * lon1 * lat1 - 0.36
                * lon1 * lat2 - 44.54 * lon3;
        double y = 200147.07 + 308807.95 * lat1 + 3745.25 * lon2 + 76.63 * lat2
                - 194.56 * lon2 * lat1 + 119.79 * lat3;

        return new Point(x, y);
    }

    public PointGeo inverse(Point p) {
        double x = p.x();
        double y = p.y();

        double x1 = (x - 600000) / 1000000;
        double y1 = (y - 200000) / 1000000;

        double x2 = x1 * x1; // => x2 = Math.pow(x1, 2)
        double x3 = x2 * x1; // => x3 = Math.pow(x1, 3)
        double y2 = y1 * y1; // => y2 = Math.pow(y1, 2)
        double y3 = y2 * y1; // => y3 = Math.pow(y1, 3)

        double lon0 = 2.6779094 + 4.728982 * x1 + 0.791484 * x1 * y1 + 0.1306
                * x1 * y2 - 0.0436 * x3;
        double lat0 = 16.9023892 + 3.238272 * y1 - 0.270978 * x2 - 0.002528
                * y2 - 0.0447 * x2 * y1 - 0.0140 * y3;

        double lon = (100 * lon0) / 36;
        double lat = (100 * lat0) / 36;

        return new PointGeo(Math.toRadians(lon), Math.toRadians(lat));
    }

}
