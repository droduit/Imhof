package ch.epfl.imhof.painting;

import java.util.function.Function;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import ch.epfl.imhof.Vector3;
import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.projection.*;
import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.dem.HGTDigitalElevationModel;

public final class HGTCanvas {
    private final int width;
    private final int height;

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
    public HGTCanvas(PointGeo bottomLeft, PointGeo topRight, int width, int height, Color bgColor) {
        this.width = width;
        this.height = height;

        Projection proj = new CH1903Projection();

        System.out.println("Canvas box:");
        System.out.println(proj.project(bottomLeft));
        System.out.println(proj.project(topRight));

        Point canvasBottomLeft = new Point(0, height);
        Point canvasTopRight   = new Point(width, 0);
        this.transform = Point.alignedCoordinateChange(
                canvasBottomLeft,
                proj.project(bottomLeft),
                canvasTopRight,
                proj.project(topRight));
        
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.ctx = image.createGraphics();

        ctx.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        ctx.setColor(bgColor.toAWTColor());
        ctx.fillRect(0, 0, width, height);
    }

    public void paint (HGTDigitalElevationModel dem) {
        Projection proj = new CH1903Projection();

        System.out.println("Transformations:");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point p = new Point(x, y);
                Point pt = this.transform.apply(p);
                // System.out.println(p);
                // System.out.println(pt);
                PointGeo pg = proj.inverse(pt);
                pg = new PointGeo(pg.latitude(), pg.longitude());
                // System.out.println(pg);
                Vector3 v = dem.normalAt(pg);

                double r = 0.5 * (v.x() + 1);
                double g = 0.5 * (v.y() + 1);
                double b = 0.5 * (v.z() + 1);

                this.ctx.setColor(Color.rgb(r, g, b).toAWTColor());
                this.ctx.drawLine(x, y, x, y);
            }
        }
    }
    
    public BufferedImage image() {
        return image;
    }
}
