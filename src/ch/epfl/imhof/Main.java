package ch.epfl.imhof;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.xml.sax.SAXException;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.osm.OSMMap;
import ch.epfl.imhof.osm.OSMMapReader;
import ch.epfl.imhof.osm.OSMToGeoTransformer;
import ch.epfl.imhof.projection.CH1903Projection;
import painting.Color;
import painting.Filters;
import painting.Java2DCanvas;
import painting.Painter;

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

        Painter painter = buildingsPainter.above(lakesPainter);
        
       
        OSMToGeoTransformer trans = new OSMToGeoTransformer(new CH1903Projection());
        OSMMap m = null;
        try {
            m = OSMMapReader.readOSMFile(Main.class.getClass().getResource("/lausanne.osm.gz").getFile(), true);
            
            Map map = trans.transform(m); // Lue depuis lausanne.osm.gz

            // La toile
            Point bl = new Point(532510, 150590);
            Point tr = new Point(539570, 155260);
            Java2DCanvas canvas =
                new Java2DCanvas(bl, tr, 800, 530, 72, Color.WHITE);

            // Dessin de la carte et stockage dans un fichier
            painter.drawMap(map, canvas);
            try {
                ImageIO.write(canvas.image(), "png", new File("loz.png"));
            } catch (IOException e) {
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
        }
        
    }
}
