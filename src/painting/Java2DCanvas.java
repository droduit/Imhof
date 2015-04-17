package painting;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;

/**
 * Mise en oeuvre concrète de toile qui dessine les primitives
 * qu'on lui demande de dessiner dans une image discrète.
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class Java2DCanvas implements Canvas {
    
    private final Point Pbl, Ptr;
    private final int width, height, dpi;
    private final Color bgColor;
    
    private final Function<Point, Point> chRep;
    private final BufferedImage image;    
    private final Graphics2D ctx;
    
    /**
     * Construit une image de la toile
     * @param Pbl Coin bas-gauche de la toile
     * @param Ptr Coin haut-droite de la toile
     * @param width Largeur de l'image de la toile (en pixels)
     * @param height Hauteur de l'image de la toile (en pixels)
     * @param dpi Résolution de l'image de la toile (en points par pouce, dpi)
     * @param bg Couleur de fond de la toile
     */
    public Java2DCanvas(Point Pbl, Point Ptr, int width, int height, int dpi, Color bgColor) {
        this.Pbl = Pbl;
        this.Ptr = Ptr;
        this.width = width;
        this.height = height;
        this.dpi = dpi;
        this.bgColor = bgColor;
        
        // Paramètres FAUX mais j'ai pas bien compris quoi passer encore...
        this.chRep = Point.alignedCoordinateChange(Pbl, Ptr, Pbl, Ptr);
        
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.ctx = image.createGraphics();
        ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    @Override
    public void drawPolyline(PolyLine p, LineStyle style) {
        
    }

    @Override
    public void drawPolygon(Polygon p, Color c) {
        ctx.setColor(c.toAWTColor());
        
    }
    
    /**
     * Permet d'obtenir l'image de la toile
     * @return Image de la toile
     */
    public BufferedImage image() {
        return image;
    }
}
