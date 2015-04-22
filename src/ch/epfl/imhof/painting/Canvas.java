package ch.epfl.imhof.painting;

import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;

/**
 * Représente une toile
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public interface Canvas {
    /**
     * Permet de dessiner sur la toile une polyligne donnée avec un style de ligne donné
     * @param p Polyligne à dessiner sur la toile
     * @param style Style de ligne
     */
    public void drawPolyline(PolyLine p, LineStyle style);
    
    /**
     * Permet de dessiner sur la toile un polygone donné avec une couleur donnée
     * @param p Polygone à dessiner sur la toile
     * @param c Couleur du polygon
     */
    public void drawPolygon(Polygon p, Color c);
}
