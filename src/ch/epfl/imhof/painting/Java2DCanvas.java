package ch.epfl.imhof.painting;

import java.util.Iterator;
import java.util.function.Function;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import ch.epfl.imhof.geometry.ClosedPolyLine;
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
    private final BufferedImage image;    
    private final Graphics2D ctx;

    private final Function<Point, Point> transform;
    
    /**
     * Construit une image de la toile
     * @param Pbl Coin bas-gauche de la toile
     * @param Ptr Coin haut-droite de la toile
     * @param width Largeur de l'image de la toile (en pixels)
     * @param height Hauteur de l'image de la toile (en pixels)
     * @param dpi Résolution de l'image de la toile (en points par pouce, dpi)
     * @param bg Couleur de fond de la toile
     */
    public Java2DCanvas(Point bottomLeft, Point topRight, int width, int height, int dpi, Color bgColor) {
        double pica = 72.0 / dpi;

        Point canvasBottomLeft = new Point(0, height);
        Point canvasTopRight   = new Point(width, 0);
        this.transform = Point.alignedCoordinateChange(bottomLeft, canvasBottomLeft, topRight, canvasTopRight);
        
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.ctx = image.createGraphics();

        ctx.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        ctx.setColor(bgColor.toAWTColor());
        ctx.fillRect(0, 0, width, height);

        ctx.scale(pica, pica);
    }
    
    @Override
    public void drawPolyline(PolyLine p, LineStyle style) {
        ctx.setColor(style.getColor().toAWTColor());
        ctx.setStroke(style.toAWTStroke());
        
        ctx.draw(getPath(p));
    }

    @Override
    public void drawPolygon(Polygon p, Color c) {
        ctx.setColor(c.toAWTColor());
        
        Area polygon = new Area(getPath(p.shell()));
        for(ClosedPolyLine hole : p.holes())
            polygon.subtract(new Area(getPath(hole)));

        ctx.fill(polygon);
    }
    
    private Path2D getPath(PolyLine p) {
        Path2D path = new Path2D.Double();
        Iterator<Point> it = p.points().iterator();
        
        /* Un PolyLine a toujours au moins un point */
        Point point = this.transform.apply(it.next());
        
        path.moveTo(point.x(), point.y());
        while (it.hasNext()) {
            point = this.transform.apply(it.next());
            path.lineTo(point.x(), point.y());
        }
        
        if (p.isClosed())
            path.closePath();
        
        return path;
    }
    
    /**
     * Permet d'obtenir l'image de la toile
     * @return Image de la toile
     */
    public BufferedImage image() {
        return image;
    }
}
