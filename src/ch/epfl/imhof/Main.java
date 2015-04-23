package ch.epfl.imhof;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import java.io.PrintWriter;
import javax.imageio.ImageIO;

import org.xml.sax.SAXException;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.osm.OSMMap;
import ch.epfl.imhof.osm.OSMMapReader;
import ch.epfl.imhof.osm.OSMToGeoTransformer;
import ch.epfl.imhof.painting.Color;
import ch.epfl.imhof.painting.Filters;
import ch.epfl.imhof.painting.Java2DCanvas;
import ch.epfl.imhof.painting.SVGCanvas;
import ch.epfl.imhof.painting.Painter;
import ch.epfl.imhof.projection.CH1903Projection;

public class Main {
    public static void main(String[] args) {
        Predicate<Attributed<?>> isLake =
            Filters.tagged("natural", "water");
        Painter lakesPainter =
            Painter.polygon(Color.BLUE).when(isLake);

        Predicate<Attributed<?>> isBuilding =
            Filters.tagged("building");
        Painter buildingsPainter =
            Painter.polygon(Color.BLACK).when(isBuilding);

        Predicate<Attributed<?>> isWood =
            Filters.tagged("natural", "wood");
        Painter woodPainter =
            Painter.polygon(Color.GREEN).when(isWood);

        Predicate<Attributed<?>> isPark =
            Filters.tagged("leisure", "park");
        Painter parkPainter =
            Painter.polygon(Color.GREEN).when(isPark);

        Predicate<Attributed<?>> isRail =
            Filters.tagged("railway");
        Painter railPainter =
            Painter.line(.6f, Color.gray(0.4)).when(isRail);

        Predicate<Attributed<?>> isRoad =
            Filters.tagged("highway");
        Painter roadPainter =
            Painter.line(.6f, Color.RED).when(isRoad);

        Painter painter = buildingsPainter
            .above(roadPainter)
            .above(railPainter)
            .above(woodPainter)
            .above(lakesPainter)
            .above(parkPainter);
       
        OSMToGeoTransformer trans = new OSMToGeoTransformer(new CH1903Projection());
        OSMMap m = null;
        try {
            m = OSMMapReader.readOSMFile(Main.class.getClass().getResource("/lausanne.osm.gz").getFile(), true);
            
            Map map = trans.transform(m); // Lue depuis lausanne.osm.gz

            // La toile
            Point bl = new Point(532510, 150590);
            Point tr = new Point(539570, 155260);
            // Java2DCanvas canvas =
            //    new Java2DCanvas(bl, tr, 5*800, 5*530, 72, Color.WHITE);
            SVGCanvas canvas = new SVGCanvas(bl, tr, 800, 530, Color.WHITE);

            // Dessin de la carte et stockage dans un fichier
            painter.drawMap(map, canvas);
            try {
                canvas.svg("loz.svg");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Aille");
            }
            
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            System.out.println("Oups");
        } catch (SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            System.out.println("Erreur");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Boom");
        }
    }
}
