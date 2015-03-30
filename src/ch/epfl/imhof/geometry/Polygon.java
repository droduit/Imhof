package ch.epfl.imhof.geometry;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Une forme géométrique composée d'une enveloppe et de trous.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class Polygon {
    /** Le contour du Polygon */
    private ClosedPolyLine shell;
    /** Les trous du Polygon */
    private List<ClosedPolyLine> holes;

    /**
     * Construit un Polygon à partir d'un contour et d'une liste de trous.
     *
     * @param shell
     *            Le contour du Polygon
     * @param holes
     *            Les trous dans le Polygon
     */
    public Polygon (ClosedPolyLine shell, List<ClosedPolyLine> holes) {
        this.shell = shell;
        this.holes = Collections.unmodifiableList(new ArrayList<>(holes));
    }

    /**
     * Retourne le contour du Polygon.
     *
     * @return Le contour de Polygon
     */
    public ClosedPolyLine shell () {
        return this.shell;
    }

    /**
     * Retourne la liste de trous dans le Polygon.
     *
     * @return La liste de trous du Polygon
     */
    public List<ClosedPolyLine> holes () {
        return this.holes;
    }

    /**
     * Construit un Polygon sans trou à partir d'un contour.
     *
     * @param shell
     *            Le contour du Polygon
     */
    public Polygon (ClosedPolyLine shell) {
        this(shell, Collections.emptyList());
    }
}
