package ch.epfl.imhof;

import java.io.File;
import javax.imageio.ImageIO;

import ch.epfl.imhof.dem.*;
import ch.epfl.imhof.painting.*;

public class HGTMain {
    public static void main (String args[]) {
        File hgtFile = new File(HGTMain.class.getClass().getResource("/N46E007.hgt").getFile());
        try (HGTDigitalElevationModel hgt = new HGTDigitalElevationModel(hgtFile)) {
            PointGeo bl = new PointGeo(Math.toRadians(7.2), Math.toRadians(46.2));
            PointGeo tr = new PointGeo(Math.toRadians(7.8), Math.toRadians(46.8));

            HGTCanvas canvas = new HGTCanvas(bl, tr, 800, 800, Color.WHITE);
            canvas.paint(hgt);
            ImageIO.write(canvas.image(), "png", new File("heightMap.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
