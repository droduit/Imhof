package ch.epfl.imhof.geometry;

import java.util.List;

/**
 * Une ligne immuable, formée par une liste de points.
 * Le premier et le dernier points étants reliés, la ligne
 * dessine alors une forme fermée.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class ClosedPolyLine extends PolyLine {
    public ClosedPolyLine (List<Point> points) {
        super(points);
    }

    public boolean isClosed() { return true; }

    /**
     * Normalise la valeur de l'index donné pour qu'il corresponde
     * à l'index d'un sommet de la PolyLine.
     *
     * @param index     L'index à normaliser, ayant une valeur dans |R
     *
     * @return L'index normalisé dans l'interval [0;points.size()[
     */
    private int generalizeIndex (int index) {
        return Math.floorMod(index, this.points().size());
    }

    /**
     * Détermine si le point p se trouve à gauche de la droite
     * formée par les points l1 et l2.
     *
     * @param l1        Le premier point de la droite
     * @param l2        Le second point de la droite
     * @param p         Le point que l'on veut situer par rapport à la droite
     *
     * @return True, si le point p est à gauche de la droite
     */
    private boolean isLeftFromLine (Point l1, Point l2, Point p) {
        double px1 = l1.x() - p.x(),
               py1 = l1.y() - p.y();
        double px2 = l2.x() - p.x(),
               py2 = l2.y() - p.y();

        return (px1 * py2 > px2 * py1);
    }

    /**
     * Calcul l'aire de la surface couverte par la PolyLine fermée.
     *
     * @return L'aire de la PolyLine fermée
     */
    public double area () {
        double area = 0.0;
        List<Point> ps = this.points();

        for (int i = 0, l = ps.size(); i < l; i++) {
            double xi = ps.get(i).x();
            double yip = ps.get( generalizeIndex(i + 1) ).y();
            double yim = ps.get( generalizeIndex(i - 1) ).y();

            area += xi * (yip - yim);
        }

        return Math.abs(area / 2);
    }

    /**
     * Détermine si le point p est contenu dans la forme de la PolyLine.
     *
     * @param p     Le point que l'on veux situer
     *
     * @return True, si le point p est contenu dans la PolyLine
     */
    public boolean containsPoint (Point p) {
        int index = 0;
        List<Point> ps = this.points();

        for (int i = 0, l = ps.size(); i < l; i++) {
            Point p1 = ps.get(i);
            Point p2 = ps.get( generalizeIndex(i + 1) );

            if (p1.y() <= p.y()) {
                if (p2.y() > p.y() && isLeftFromLine(p1, p2, p)) {
                    index += 1;
                }
            } else {
                if (p2.y() <= p.y() && isLeftFromLine(p2, p1, p)) {
                    index -= 1;
                }
            }
        }
        
        return (index != 0);
    }
}
