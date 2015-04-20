package painting;

import java.util.List;
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
        double pica = dpi / 72.0;

        Point canvasBottomLeft = new Point(-pica, -pica);
        Point canvasTopRight   = new Point( pica,  pica);
        this.transform = Point.alignedCoordinateChange(bottomLeft, canvasBottomLeft, topRight, canvasTopRight);
        
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.ctx = image.createGraphics();

        ctx.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        ctx.setColor(bgColor.toAWTColor());
        ctx.fillRect(0, 0, width, height);

        ctx.scale(width / pica, height / pica);
        ctx.translate(0.5, 0.5);
    }
    
    @Override
    public void drawPolyline(PolyLine p, LineStyle style) {
        ctx.setColor(style.getColor().toAWTColor());
        ctx.setStroke(style.toAWTStroke());
        
        Path2D path = getPath(p);
        ctx.fill(path);
    }

    @Override
    public void drawPolygon(Polygon p, Color c) {
        ctx.setColor(c.toAWTColor());
        
        Path2D shell = getPath(p.shell());
        
        Area polygon = new Area(shell);
        
        for(ClosedPolyLine hole : p.holes()) {
            polygon.subtract(new Area(getPath(hole)));
        }
    }
    
    private Path2D getPath(PolyLine p) {
        Path2D path = new Path2D.Double();
        
        List<Point> points = p.points();
        Iterator<Point> it = points.iterator();
        
        Point firstPoint = null;
        if(it.hasNext()) 
            firstPoint = it.next();
        
        path.moveTo(firstPoint.x(), firstPoint.y());
       
        while(it.hasNext()) {
            Point point = it.next();
            path.lineTo(point.x(), point.y());
        }
        
        if(p.isClosed())
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
