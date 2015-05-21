package ch.epfl.imhof.dem;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.Vector3;

/**
 * Modèle numérique du terrain
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public interface DigitalElevationModel extends AutoCloseable {
    /**
     * Retourne le vecteur normal à la Terre au point spécifié
     * 
     * @param point
     *            Point en coordonnées WGS84 pour lequel on veut le vecteur
     *            normal à la terre
     * @return Vecteur normal à la Terre au point spécifié
     */
    public Vector3 normalAt(PointGeo point);
}
