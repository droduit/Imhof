package ch.epfl.imhof.geometry;

import java.util.List;

/**
 * Une ligne immuable, formée par une liste de points. Le premier et le dernier
 * points n'étants pas reliés, la ligne ne dessine pas une forme fermée.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class OpenPolyLine extends PolyLine {
    /**
     * Construit une polyligne ouverte de sommets donnés
     * 
     * @param points
     *            Sommets de la polyligne ouverte
     */
    public OpenPolyLine(List<Point> points) {
        super(points);
    }

    public boolean isClosed() {
        return false;
    }
}
